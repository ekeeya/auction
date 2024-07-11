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

package com.skycastle.auction.tasks;

import com.skycastle.auction.dtos.airtel.responses.PaymentResponseDTO;
import com.skycastle.auction.dtos.easypay.response.EasypayStatusResponse;
import com.skycastle.auction.dtos.mtn.responses.RequestToPayStatusResponseDTO;
import com.skycastle.auction.entities.*;
import com.skycastle.auction.entities.mm.APIUser;
import com.skycastle.auction.entities.mm.AirtelApiUser;
import com.skycastle.auction.entities.mm.EasyPayApiUser;
import com.skycastle.auction.entities.mm.MTNApiUser;
import com.skycastle.auction.services.external.ExternalRequests;
import com.skycastle.auction.services.mm.MobileMoneyService;
import com.skycastle.auction.services.mm.PaymentTransactionService;
import com.skycastle.auction.services.subscription.SubscriptionService;
import com.skycastle.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.skycastle.auction.utils.Utils.SUBSCRIPTION_STATE.INACTIVE;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableAsync
public class ScheduleTasks {

    private final PaymentTransactionService transactionService;
    private final ExternalRequests externalRequests;
    private final SubscriptionService subscriptionService;
    private final MobileMoneyService mobileMoneyService;
    @Async
    @Scheduled(fixedRate = 5000)
    @Transactional
    public void activateSubscriptions() throws InstantiationException, IllegalAccessException {
        log.info("Probing pending transactions");
        List<PaymentTransaction> transactionList = transactionService.getPendingTransactions();

        for (PaymentTransaction transaction: transactionList) {
            APIUser apiUser = mobileMoneyService.getDefaultApiUser(transaction.getProvider());
            Subscription sub = transaction.getSubscription();
            if(transaction.getProvider().equals(Utils.PROVIDER.MTN)){
                MTNApiUser user = (MTNApiUser) apiUser;
                MTNTransaction t = (MTNTransaction) transaction;
                RequestToPayStatusResponseDTO transStatus = externalRequests.getRequestToPayStatus(t.getXReferenceId(),user);
                if (transStatus.getStatus().equals(RequestToPayStatusResponseDTO.MOMO_TRANS_STATUS.SUCCESSFUL)){
                    // activate a Subscription
                    Subscription subscription = subscriptionService.activateSubscription(sub);
                    String msg = String.format("Subscription: %s has been activated", subscription);
                    // TODO send email to user
                    log.info(msg);
                    // change transaction state
                    transaction.setStatus(Utils.TRANSACTION_STATUS.SUCCESS);
                } else if (transStatus.getStatus().equals(RequestToPayStatusResponseDTO.MOMO_TRANS_STATUS.PENDING)) {
                    // Do nothing
                }else{
                    transaction.setStatus(Utils.TRANSACTION_STATUS.FAILED);
                    log.error("Failed transaction: "+ transStatus);
                    sub.setState(INACTIVE);
                    sub.setEnabled(false);
                }
            }else if(transaction.getProvider().equals(Utils.PROVIDER.AIRTEL)){
                AirtelApiUser user =  (AirtelApiUser)  apiUser;
                AirtelTransaction t =  (AirtelTransaction) transaction;
                PaymentResponseDTO response =externalRequests.airtelTransactionInquiry(user, t.getTransactionId());
                // this is not tested
                if(response.getStatus().getResponse_code().equals(Utils.AIRTEL_CODES.DP00800001001)){
                    //
                    Subscription subscription = subscriptionService.activateSubscription(sub);
                    String msg = String.format("Subscription: %s has been activated", subscription);
                    // TODO send email to user
                    log.info(msg);
                    // change transaction state
                    transaction.setStatus(Utils.TRANSACTION_STATUS.SUCCESS);
                } else if (response.getStatus().getResponse_code().equals(Utils.AIRTEL_CODES.DP00800001006)){
                    // Pending do nothing
                }
                else{
                    //
                    transaction.setStatus(Utils.TRANSACTION_STATUS.FAILED);
                    log.error("Failed transaction: "+ response);
                }
            }else{
                // Easy pay
                EasyPayApiUser user =  (EasyPayApiUser)  apiUser;
                EasyPayTransaction t = (EasyPayTransaction) transaction;
                EasypayStatusResponse response = externalRequests.easyTransactionStatus(user, t.getTransactionId());
                if (response.getSuccess() == 0) {
                    transaction.setStatus(Utils.TRANSACTION_STATUS.FAILED);
                    log.error("Failed transaction: "+ response);
                }
                else if(response.getData().getStatus().equals("Success")){
                    Subscription subscription = subscriptionService.activateSubscription(sub);
                    String msg = String.format("Subscription: %s has been activated", subscription);
                    // TODO send email to user
                    log.info(msg);
                    // change transaction state
                    transaction.setStatus(Utils.TRANSACTION_STATUS.SUCCESS);
                }

            }
        }
    }
}
