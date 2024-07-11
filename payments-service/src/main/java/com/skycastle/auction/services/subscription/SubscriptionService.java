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

import com.skycastle.auction.dtos.User;
import com.skycastle.auction.entities.PaymentTransaction;
import com.skycastle.auction.entities.Subscription;
import com.skycastle.auction.utils.Utils;

import java.util.Date;

public interface SubscriptionService {

    Long deactivateSubscription(Long subscriptionId);
    Subscription openSubscription(User user, PaymentTransaction transaction);
    Subscription activateSubscription(Subscription subscription);
    Utils.PROVIDER determineProvider(String msisdn) throws Exception;
    Subscription setEndDate(Subscription sub, Date startDate);
}
