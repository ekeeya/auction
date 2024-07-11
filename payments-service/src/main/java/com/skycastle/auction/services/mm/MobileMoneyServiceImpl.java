/*
 * Online auctioning system
 * Copyright (C) 2023 - , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>
 *
 * This program is not free software
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 */

package com.skycastle.auction.services.mm;


import com.skycastle.auction.dtos.User;
import com.skycastle.auction.dtos.BaseRequestToPay;
import com.skycastle.auction.dtos.airtel.requests.AirtelAccessTokenRequestDTO;
import com.skycastle.auction.dtos.airtel.requests.AirtelPaymentRequestDTO;
import com.skycastle.auction.dtos.airtel.requests.AirtelRequestToPayDTO;
import com.skycastle.auction.dtos.airtel.responses.PaymentResponseDTO;
import com.skycastle.auction.dtos.easypay.constants;
import com.skycastle.auction.dtos.easypay.requests.EasyPayRequestToPayDTO;
import com.skycastle.auction.dtos.easypay.requests.EasyPaymentRequestDTO;
import com.skycastle.auction.dtos.easypay.response.EasyPayResponseDTO;
import com.skycastle.auction.dtos.mtn.requests.MomoRequestToPayDTO;
import com.skycastle.auction.dtos.mtn.responses.APIkeyResponseDTO;
import com.skycastle.auction.dtos.AccessTokenResponseDTO;
import com.skycastle.auction.dtos.mtn.responses.EmptyResponseDTO;
import com.skycastle.auction.entities.*;
import com.skycastle.auction.dtos.mtn.requests.MomoAccessTokenRequestDTO;
import com.skycastle.auction.entities.forms.requests.MobileMoneyProductConfigDTO;
import com.skycastle.auction.entities.mm.*;
import com.skycastle.auction.repositories.mm.APIUserRepository;
import com.skycastle.auction.repositories.mm.MMProductRepository;
import com.skycastle.auction.repositories.payments.PaymentTransactionRepository;
import com.skycastle.auction.repositories.subscriptions.SubscriptionRepository;
import com.skycastle.auction.services.CustomRunnable;
import com.skycastle.auction.services.IdentifiableRunnable;
import com.skycastle.auction.services.TransactionalExecutorService;
import com.skycastle.auction.services.external.ExternalRequests;
import com.skycastle.auction.services.subscription.SubscriptionService;
import com.skycastle.auction.utils.ContextProvider;
import com.skycastle.auction.utils.Utils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MobileMoneyServiceImpl implements MobileMoneyService {

    private final MMProductRepository mmProductRepository;
    private final APIUserRepository apiUserRepository;
    private final ExternalRequests externalRequests;
    private final TransactionalExecutorService executorService;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final EntityManager entityManager;

    private final SubscriptionRepository subscriptionRepository;

    private final SubscriptionService subscriptionService;

    private final ContextProvider contextProvider;

    @Override
    public Long configureMobileMoneyInTransaction(MobileMoneyProductConfigDTO config) {

        Function<Long, Long> future = x -> {
            MobileMoneyProduct product;
            APIUser apiUser;

            if (config.getProvider().equals(Utils.PROVIDER.MTN)) {
                //  configure and return for MTN
                product = new MTNProduct();
                ((MTNProduct) product).setDescription(config.getDescription());
            } else if(config.getProvider().equals(Utils.PROVIDER.AIRTEL)){
                product = new AirtelProduct();
                ((AirtelProduct) product).setHashKey(config.getHashKey());
            }
            else{
                // For easy-pay we only need one product
                product =  mmProductRepository.findMobileMoneyProductByProviderAndProductType(config.getProvider(), config.getProductType());
                if(product == null){
                    product = new EasyPayProduct();
                }
            }
            product.setProductType(config.getProductType());
            product.setName(config.getName());
            product.setProvider(config.getProvider());
            product.setCallBackUrl(config.getCallBackUrl());
            mmProductRepository.save(product);
            // create User
            try {
                AccessTokenResponseDTO response;
                // now we have the user UUID let's generate the API Key
                switch (product.getProvider()){
                    case MTN:
                        String userUuid = externalRequests.sendCreateMomoAPIUser(config.getPrimaryKey(), config.getCallBackUrl());
                        APIkeyResponseDTO apiKeyResponse = externalRequests.generateMomoAPIKey(userUuid, config.getPrimaryKey());
                        apiUser = new MTNApiUser();
                        ((MTNApiUser) apiUser).setApiKey(apiKeyResponse.getApiKey());
                        ((MTNApiUser) apiUser).setUserUuid(userUuid);
                        ((MTNApiUser) apiUser).setPrimaryKey(config.getPrimaryKey());
                        ((MTNApiUser) apiUser).setSecondaryKey(config.getSecondaryKey());
                        // We might as well get he accessToken
                        MomoAccessTokenRequestDTO momoAccessTokenRequestDTO = new MomoAccessTokenRequestDTO();
                        momoAccessTokenRequestDTO.setProvider(config.getProvider());
                        momoAccessTokenRequestDTO.setPrimaryKey(config.getPrimaryKey());
                        momoAccessTokenRequestDTO.setApiKey(apiKeyResponse.getApiKey());
                        momoAccessTokenRequestDTO.setUserUuid(userUuid);
                        apiUser.setAccessTokenAdded(Calendar.getInstance().getTime());
                        response = externalRequests.generateMomoAccessToken(momoAccessTokenRequestDTO);
                        apiUser.setAccessToken(response.getAccess_token());
                        apiUser.setAccessTokenExpiresIn(response.getExpires_in());
                        break;
                    case AIRTEL:
                        apiUser = new AirtelApiUser();
                        apiUser.setAccessTokenAdded(Calendar.getInstance().getTime());
                        AirtelAccessTokenRequestDTO accessTokenRequest = new AirtelAccessTokenRequestDTO(config.getClientId(), config.getClientSecretKey());
                        response = externalRequests.generateAirtelAccessToken(accessTokenRequest);
                        ((AirtelApiUser) apiUser).setClientId(config.getClientId());
                        ((AirtelApiUser) apiUser).setClientSecretKey(config.getClientSecretKey());
                        apiUser.setAccessToken(response.getAccess_token());
                        apiUser.setAccessTokenExpiresIn(response.getExpires_in());
                        break;
                    default:
                        // This is EASYPAY
                        apiUser = new EasyPayApiUser();
                        ((EasyPayApiUser) apiUser).setUsername(config.getUsername());
                        ((EasyPayApiUser) apiUser).setPassword(config.getPassword());
                        break;
                }

                apiUser.setEnvironment(config.getEnvironment());
                apiUser.setProvider(config.getProvider());
                apiUser.setProduct(product);
                APIUser newUser = apiUserRepository.save(apiUser);
                return newUser.getId();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
        IdentifiableRunnable customRunnable = new CustomRunnable(future);
        return executorService.executeInTransaction(customRunnable);
    }

    @Override
    public <T extends MobileMoneyProduct> T getProduct(Utils.PRODUCT_TYPE type, Utils.PROVIDER provider) {
        return (T) mmProductRepository.findMobileMoneyProductByProviderAndProductType(provider, type);
    }

    @Override
    public <T extends MobileMoneyProduct> T getProduct(Long productId) {
        try {
            MobileMoneyProduct p = mmProductRepository.findById(productId).get();
            return (T) p;
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("No such Mobile money product with id: " + productId);
        }
    }

    @Override
    public <T extends APIUser> T getApiUser(MobileMoneyProduct product) {
        return (T) apiUserRepository.findAPIUserByProductAndProvider(product, product.getProvider());
    }

    @Override
    public <T extends APIUser> T getDefaultApiUser(Utils.PROVIDER provider) {
        return (T) apiUserRepository.findAPIUserByProduct_ProductTypeAndProvider(Utils.PRODUCT_TYPE.COLLECTIONS, provider);
    }


    @Override
    public APIUser refreshAccessToken(APIUser user) throws InstantiationException, IllegalAccessException {
        Calendar tokenCreationDate = Calendar.getInstance();
        tokenCreationDate.setTime(user.getAccessTokenAdded());
        tokenCreationDate.add(Calendar.SECOND, (user.getAccessTokenExpiresIn() + 30));// let's add in more 30s
        Date now = Calendar.getInstance().getTime();
        AccessTokenResponseDTO response;
        if (tokenCreationDate.getTime().after(now)) {
            if (user.getProvider().equals(Utils.PROVIDER.MTN)) {
                MomoAccessTokenRequestDTO token = new MomoAccessTokenRequestDTO();
                MTNApiUser u = (MTNApiUser) user;
                token.setApiKey(u.getApiKey());
                token.setProvider(Utils.PROVIDER.MTN);
                token.setPrimaryKey(u.getPrimaryKey());
                token.setUserUuid(u.getUserUuid());
                response = externalRequests.generateMomoAccessToken(token);
            } else {
                AirtelApiUser u = (AirtelApiUser) user;
                AirtelAccessTokenRequestDTO token =  new AirtelAccessTokenRequestDTO(u.getClientId(),u.getClientSecretKey());
                response =  externalRequests.generateAirtelAccessToken(token);
            }
            user.setAccessTokenAdded(Calendar.getInstance().getTime());

            user.setAccessToken(response.getAccess_token());
            user.setAccessTokenExpiresIn(response.getExpires_in());
            return apiUserRepository.save(user);
        }
        return  user;
    }

    @Override
    public Long initiateSubscriptionPayment(BaseRequestToPay request, Utils.ENV env) {
        Function<Long, Long> future = x -> {
            PaymentTransaction transaction;
            try {
                long random = (long) (Math.random() * 1000000000000000L);
                String referenceId = String.format("REF-%s", random);
                MobileMoneyProduct product = getProduct(Utils.PRODUCT_TYPE.COLLECTIONS, request.getProvider());
                APIUser apiUser =  refreshAccessToken(getApiUser(product));
                if (request.getProvider().equals(Utils.PROVIDER.MTN)) {
                    transaction = new MTNTransaction();
                    MomoRequestToPayDTO req = (MomoRequestToPayDTO) request;
                    req.setExternalId(referenceId);
                    String currency = env.equals(Utils.ENV.SANDBOX) ? "EUR" : "UGX";
                    String payerMessage = String.format("Make an annual subscription payment of UGX:%s to SkyCastle Auctions", request.getAmount());
                    String payeeNote = String.format("Receive an annual subscription payment of UGX:%s from %s", request.getAmount(), ((MomoRequestToPayDTO) request).getPayer().getPartyId());
                    req.setPayeeNote(payeeNote);
                    req.setPayerMessage(payerMessage);
                    req.setCurrency(currency);
                    MTNApiUser mtnApiUser = (MTNApiUser)apiUser;
                    // let's make the request but before, let's check the token activeness
                    EmptyResponseDTO response = externalRequests.momoRequestToPay(req, mtnApiUser);
                    log.info(response.toString());
                    ((MTNTransaction) transaction).setXReferenceId(response.getXReferenceId());
                    transaction.setMsisdn(req.getPayer().getPartyId());
                } else if (request.getProvider().equals(Utils.PROVIDER.EASYPAY)) {
                     transaction = new EasyPayTransaction();
                     EasyPayApiUser easyPayApiUser = (EasyPayApiUser) apiUser;
                    String reason = String.format("Make an annual subscription payment of UGX:%s to SkyCastle Auctions", request.getAmount());
                    EasyPayRequestToPayDTO r = (EasyPayRequestToPayDTO)  request;
                    EasyPaymentRequestDTO req =  new EasyPaymentRequestDTO(
                            easyPayApiUser.getUsername(),
                            easyPayApiUser.getPassword(),
                            constants.ACTIONS.MM_DEPOSIT,
                            request.getAmount(),
                            "UGX",
                            r.getPhone(),
                            referenceId,
                            reason );
                    EasyPayResponseDTO response = externalRequests.easyInitiatePayment(easyPayApiUser, req);
                    log.info(""+response);
                    transaction.setMsisdn(r.getPhone());
                } else {
                    transaction = new AirtelTransaction();
                    AirtelRequestToPayDTO r = (AirtelRequestToPayDTO) request;
                    String reference = String.format("Initiate payment of %S for from %s", request.getAmount(), r.getMsisdn());
                    AirtelPaymentRequestDTO req = new AirtelPaymentRequestDTO(r.getMsisdn(), request.getAmount(), referenceId, reference);
                    AirtelApiUser airtelApiUser =  (AirtelApiUser) apiUser;
                    PaymentResponseDTO  response =  externalRequests.airtelInitiatePayment(airtelApiUser, req);
                    log.info(response.toString());
                    ((AirtelTransaction) transaction).setResponseCode(response.getData().get("transaction").get("id"));
                    transaction.setMsisdn(((AirtelRequestToPayDTO) request).getMsisdn());
                }

                transaction.setAmount(BigDecimal.valueOf(request.getAmount()));
                transaction.setCurrency("UGX");
                transaction.setProvider(request.getProvider());
                transaction.setTransactionId(referenceId);
                User user = contextProvider.getPrincipal();
                // let's create the subscription;
                Subscription sub = subscriptionService.openSubscription(user, transaction);
                transaction.setSubscription(sub);
                transaction.setUserId(user.getId());
                PaymentTransaction t = paymentTransactionRepository.save(transaction);
                return t.getId();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };
        IdentifiableRunnable customRunnable = new CustomRunnable(future);
        return executorService.executeInTransaction(customRunnable);
    }
}
