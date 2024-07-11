/*
 * Online auctioning system
 *
 * Copyright (c) 2023.  Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.controllers;

import com.skycastle.auction.dtos.easypay.requests.EasyPayRequestToPayDTO;
import com.skycastle.auction.dtos.mtn.Payer;
import com.skycastle.auction.dtos.airtel.requests.AirtelRequestToPayDTO;
import com.skycastle.auction.dtos.BaseRequestToPay;
import com.skycastle.auction.dtos.mtn.requests.MomoRequestToPayDTO;
import com.skycastle.auction.entities.PaymentTransaction;
import com.skycastle.auction.entities.forms.requests.SubscriptionPaymentRequest;
import com.skycastle.auction.entities.forms.responses.BaseResponseDTO;
import com.skycastle.auction.repositories.payments.PaymentTransactionRepository;
import com.skycastle.auction.services.mm.MobileMoneyService;
import com.skycastle.auction.services.subscription.SubscriptionService;
import com.skycastle.auction.services.subscription.SubscriptionServiceImpl;
import com.skycastle.auction.utils.Utils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class PaymentController {

    private final MobileMoneyService mobileMoneyService;

    private final PaymentTransactionRepository transactionRepository;

    private final SubscriptionService subscriptionService;

    @PostMapping("/pay-subscription")
    public ResponseEntity<BaseResponseDTO> subscriptionPayment(@RequestBody @Valid SubscriptionPaymentRequest request, BindingResult result) {
        BaseResponseDTO response = new BaseResponseDTO(result);
        try {
            if (response.isSuccess()) {
                BaseRequestToPay requestToPay;
                Utils.PROVIDER provider =  request.getIsTelecom() ? subscriptionService.determineProvider(request.getMsisdn()) : Utils.PROVIDER.EASYPAY;
                String msisdn;
                switch (provider){
                    case MTN :
                        msisdn = SubscriptionServiceImpl.sanitizeMsisdn(request.getMsisdn(), Utils.PROVIDER.MTN);
                        Payer payer =  new Payer();
                        payer.setPartyId(msisdn);
                        payer.setPartyIdType("MSISDN");
                        requestToPay = new MomoRequestToPayDTO();
                        ((MomoRequestToPayDTO) requestToPay).setPayer(payer);
                        ((MomoRequestToPayDTO) requestToPay).setCurrency("EUR");
                    case AIRTEL:
                        msisdn = SubscriptionServiceImpl.sanitizeMsisdn(request.getMsisdn(), Utils.PROVIDER.AIRTEL);
                        requestToPay = new AirtelRequestToPayDTO();
                        ((AirtelRequestToPayDTO) requestToPay).setMsisdn(msisdn);
                    default:
                        msisdn = SubscriptionServiceImpl.sanitizeMsisdn(request.getMsisdn(), Utils.PROVIDER.EASYPAY);
                        requestToPay =  new EasyPayRequestToPayDTO();
                        requestToPay.setAmount(request.getAmount());
                        requestToPay.setProvider(Utils.PROVIDER.EASYPAY);
                        ((EasyPayRequestToPayDTO) requestToPay).setPhone(msisdn);
                }

                requestToPay.setAmount(request.getAmount());
                requestToPay.setProvider(provider);
                Long paymentId = mobileMoneyService.initiateSubscriptionPayment(requestToPay, request.getEnv());
                PaymentTransaction paymentTransaction = transactionRepository.findById(paymentId).get(); // this exception can not happen at this level
                response.setMessage("Request to pay has been Sent");
                response.setData(paymentTransaction);
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            response = new BaseResponseDTO(e);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
