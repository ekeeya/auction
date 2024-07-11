/*
 * Online auctioning system
 *
 * Copyright (c)  $today.year- , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.services.refreshTokens;

import com.skycastle.auction.entities.tokens.RefreshToken;

public interface RefreshTokenService {

    void revokeToken(String token);
    boolean isActiveToken(String token);
    void delete(RefreshToken token);
    void  save(RefreshToken token);
}
