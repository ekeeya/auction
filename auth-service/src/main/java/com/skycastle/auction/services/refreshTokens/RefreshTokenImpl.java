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
import com.skycastle.auction.repositories.RefreshTokensRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class RefreshTokenImpl implements RefreshTokenService{

    private final RefreshTokensRepository refreshTokensRepository;
    @Override
    public void revokeToken(String token) {
        try{
            RefreshToken refreshToken = refreshTokensRepository.findRefreshTokenByToken(token);
            if(refreshToken != null){
                delete(refreshToken);
            }

        }catch (Exception e){
            log.warn("Revoke token failed due to: {}", e.getMessage());
        }
    }

    @Override
    public boolean isActiveToken(String token) {
        RefreshToken refreshToken = refreshTokensRepository.findRefreshTokenByToken(token);
        return refreshToken != null ? refreshToken.getEnabled() : false;
    }

    @Override
    public void delete(RefreshToken token) {
        refreshTokensRepository.delete(token);
    }

    @Override
    public void save(RefreshToken token) {
        refreshTokensRepository.save(token);
    }
}
