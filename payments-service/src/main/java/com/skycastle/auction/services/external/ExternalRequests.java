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

package com.skycastle.auction.services.external;

import com.skycastle.auction.dtos.airtel.requests.AirtelAccessTokenRequestDTO;
import com.skycastle.auction.dtos.airtel.requests.AirtelPaymentRequestDTO;
import com.skycastle.auction.dtos.airtel.requests.AirtelRequestToPayDTO;
import com.skycastle.auction.dtos.airtel.responses.PaymentResponseDTO;
import com.skycastle.auction.dtos.easypay.requests.EasyPayRequestToPayDTO;
import com.skycastle.auction.dtos.easypay.requests.EasyPaymentRequestDTO;
import com.skycastle.auction.dtos.easypay.requests.EasypayStatusRequest;
import com.skycastle.auction.dtos.easypay.response.EasyPayResponseDTO;
import com.skycastle.auction.dtos.easypay.response.EasypayStatusResponse;
import com.skycastle.auction.dtos.mtn.requests.MomoRequestToPayDTO;
import com.skycastle.auction.dtos.mtn.responses.APIkeyResponseDTO;
import com.skycastle.auction.dtos.AccessTokenResponseDTO;
import com.skycastle.auction.dtos.mtn.responses.EmptyResponseDTO;
import com.skycastle.auction.dtos.mtn.responses.RequestToPayStatusResponseDTO;
import com.skycastle.auction.dtos.mtn.requests.MomoAccessTokenRequestDTO;
import com.skycastle.auction.entities.mm.AirtelApiUser;
import com.skycastle.auction.entities.mm.EasyPayApiUser;
import com.skycastle.auction.entities.mm.MTNApiUser;

public interface ExternalRequests {

    String  sendCreateMomoAPIUser(String primaryKey, String callBack) throws InstantiationException, IllegalAccessException;
    APIkeyResponseDTO generateMomoAPIKey(String userUUID, String primaryKey) throws InstantiationException, IllegalAccessException;
    AccessTokenResponseDTO generateMomoAccessToken(MomoAccessTokenRequestDTO request) throws InstantiationException, IllegalAccessException;
    EmptyResponseDTO momoRequestToPay(MomoRequestToPayDTO request, MTNApiUser user) throws InstantiationException, IllegalAccessException;
    RequestToPayStatusResponseDTO getRequestToPayStatus(String transactionReferenceId,MTNApiUser user) throws InstantiationException, IllegalAccessException;

    AccessTokenResponseDTO generateAirtelAccessToken(AirtelAccessTokenRequestDTO request) throws InstantiationException, IllegalAccessException;

    PaymentResponseDTO airtelInitiatePayment(AirtelApiUser user, AirtelPaymentRequestDTO request) throws InstantiationException, IllegalAccessException;
    PaymentResponseDTO airtelTransactionInquiry(AirtelApiUser user, String transactionId) throws InstantiationException, IllegalAccessException;

    EasyPayResponseDTO easyInitiatePayment(EasyPayApiUser user, EasyPaymentRequestDTO request) throws InstantiationException, IllegalAccessException;

    EasypayStatusResponse easyTransactionStatus(EasyPayApiUser user, String  reference) throws InstantiationException, IllegalAccessException;

}
