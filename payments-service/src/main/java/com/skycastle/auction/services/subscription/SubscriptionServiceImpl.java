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

package com.skycastle.auction.services.subscription;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.skycastle.auction.dtos.User;
import com.skycastle.auction.entities.PaymentTransaction;
import com.skycastle.auction.entities.Subscription;
import com.skycastle.auction.repositories.subscriptions.SubscriptionRepository;
import com.skycastle.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService{

    private final SubscriptionRepository subscriptionRepository;

    @Value("${mm.api.airtelPrefixes}")
    private List<String> airtelPrefixes;

    @Value("${mm.api.mtnPrefixes}")
    private List<String> mtnPrefixes;

    @Value("${subscription.duration}")
    private int subscriptionDuration; // in months

    public static String sanitizeMsisdn(String tel, Utils.PROVIDER provider) throws Exception {
        if (tel.length()  < 10 || tel.length() > 13){
            String error = String.format("Bad phone number length for: %s", tel);
            throw new Exception(error);
        }
        if (provider != Utils.PROVIDER.MTN){
            if (tel.startsWith("0")){
                return String.format("256%s", tel.substring(1,tel.length()));
            } else if (tel.startsWith("+256")) {
                return tel.substring(1,tel.length());
            }
        }else{
            if (tel.startsWith("0")){
                return tel.substring(1,tel.length());
            } else if (tel.startsWith("+256")) {
                return tel.substring(4,tel.length());
            }
        }

        return tel.substring(3,tel.length());
    }

    public static String noPrefixMsisdn(String tel){
        if (tel.startsWith("0")){
            return tel.substring(1,tel.length());
        } else if (tel.startsWith("+256")) {
            return tel.substring(4,tel.length());
        }
        return tel.substring(3,tel.length());
    }
    @Override
    public Long deactivateSubscription(Long subscriptionId) {
        Subscription subscription =  subscriptionRepository.findById(subscriptionId).get();
        subscription.setEnabled(false);
        return subscription.getId();
    }

    @Override
    public Subscription openSubscription(User user, PaymentTransaction transaction) {
        try{
            Subscription sub =  subscriptionRepository.findSubscriptionByUserId(user.getId());
            if (sub != null){
                sub.setAmount(transaction.getAmount());
                sub.setStartDate(transaction.getCreatedAt());
                sub = setEndDate(sub, sub.getStartDate());
                sub.setEnabled(false);
            }else{
                sub = new Subscription();
                Utils.SUBSCRIPTION_TYPE type =  user.getRole().equals(Utils.ROLES.ROLE_BUYER) ? Utils.SUBSCRIPTION_TYPE.BUYER  : Utils.SUBSCRIPTION_TYPE.SELLER;
                sub.setType(type);
                sub.setStartDate(Calendar.getInstance().getTime());
                sub =  setEndDate(sub, sub.getStartDate());
            }
            ObjectMapper oMapper = new ObjectMapper();
            user.setToken(null);// clear the token
            Map<String, Object> userDetails =  oMapper.convertValue(user, Map.class);
            sub.setUserDetails(userDetails);
            sub.setUserId(user.getId());
            sub.setEnabled(false);
            sub.setState(Utils.SUBSCRIPTION_STATE.PENDING);
            transaction.setSubscription(sub);
            return subscriptionRepository.save(sub);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public Subscription activateSubscription(Subscription subscription) {
        subscription.setEnabled(true);
        subscription.setState(Utils.SUBSCRIPTION_STATE.ACTIVE);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Utils.PROVIDER determineProvider(String msisdn) throws Exception {
        String number = noPrefixMsisdn(msisdn);
        String prefix  =  number.substring(0, 2);
        if(airtelPrefixes.contains(prefix)){
            return Utils.PROVIDER.AIRTEL;
        } else if (mtnPrefixes.contains(prefix)) {
            return Utils.PROVIDER.MTN;
        }
        throw new Exception("Unsupported provider for tel: "+msisdn);
    }

    @Override
    public Subscription setEndDate(Subscription sub, Date startDate) {
        Date sDate = sub.getStartDate();
        Calendar sc = Calendar.getInstance();
        sc.setTime(sDate);
        sc.add(Calendar.MONTH, subscriptionDuration);
        sub.setEndDate(sc.getTime());
        return sub;
    }
}
