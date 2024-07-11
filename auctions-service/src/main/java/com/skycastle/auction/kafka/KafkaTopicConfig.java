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

package com.skycastle.auction.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic topic(){
        return TopicBuilder.name("auction-session")
                .build();
    }

    @Bean
    public NewTopic topic2(){
        return TopicBuilder.name("auction-session-chat")
                .build();
    }

    @Bean
    public RecordMessageConverter recordMessageConverter() {
        return new ByteArrayJsonMessageConverter();
    }
}
