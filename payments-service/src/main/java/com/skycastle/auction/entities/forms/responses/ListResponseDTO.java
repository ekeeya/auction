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

package com.skycastle.auction.entities.forms.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;


@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class ListResponseDTO<T> extends BaseResponseDTO {

    private List<T> entries = new ArrayList<>();
    private long totalCount;
    private int offset;
    private int totalPages;
    private int size;
    private int page;
    public ListResponseDTO(List<T> entries){
        this.entries = entries;
        setSize(entries.size());
        this.setStatusCode(HttpStatus.OK.value());
    }

    // Set errors if any
    public ListResponseDTO(HttpStatus status, String errors){
        if (!status.equals(HttpStatus.OK)){
            this.setTotalPages(0);
            this.setTotalCount(0);
            this.setSize(0);
            this.setOffset(0);
            this.setStatusCode(status.value());
            this.setMessage(errors);
        }
    }
}
