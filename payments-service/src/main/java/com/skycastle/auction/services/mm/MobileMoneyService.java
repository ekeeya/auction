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

import com.skycastle.auction.dtos.BaseRequestToPay;
import com.skycastle.auction.entities.forms.requests.MobileMoneyProductConfigDTO;
import com.skycastle.auction.entities.mm.APIUser;
import com.skycastle.auction.entities.mm.MTNApiUser;
import com.skycastle.auction.entities.mm.MobileMoneyProduct;
import com.skycastle.auction.utils.Utils;

public interface MobileMoneyService {
    Long configureMobileMoneyInTransaction(MobileMoneyProductConfigDTO config) ;
    <T extends MobileMoneyProduct> T getProduct(Utils.PRODUCT_TYPE type, Utils.PROVIDER provider);
    <T extends MobileMoneyProduct> T getProduct(Long productId);
    <T extends  APIUser> T getApiUser(MobileMoneyProduct product);
    <T extends  APIUser> T getDefaultApiUser(Utils.PROVIDER provider);
    Long initiateSubscriptionPayment(BaseRequestToPay request, Utils.ENV env);
    APIUser refreshAccessToken(APIUser user) throws InstantiationException, IllegalAccessException;
}
