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

package com.skycastle.auction.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionalExecutorServiceImpl implements TransactionalExecutorService{


    private final TransactionTemplate transactionTemplate;
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void execInTransaction(Runnable r) {
        try {
            r.run();
        } catch (Exception ex) {
            log.error("Failed to execute transaction: ", ex);
        }
    }

    @Override
    public <T> Long executeInTransaction(IdentifiableRunnable runnable) {
        return transactionTemplate.execute(status -> {
            runnable.run();
            // assume the ID is returned from the runnable somehow
            return runnable.getId();
        });
    }
}
