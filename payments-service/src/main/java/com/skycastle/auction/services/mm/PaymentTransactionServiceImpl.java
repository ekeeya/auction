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

import com.skycastle.auction.entities.PaymentTransaction;
import com.skycastle.auction.repositories.payments.PaymentTransactionRepository;
import com.skycastle.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private final PaymentTransactionRepository transactionRepository;
    @Override
    public List<PaymentTransaction> getPendingTransactions() {
        return transactionRepository.findPaymentTransactionsByStatus(Utils.TRANSACTION_STATUS.PENDING);
    }
}
