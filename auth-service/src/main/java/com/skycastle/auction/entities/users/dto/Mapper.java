/*
 * Online auctioning system
 *
 * Copyright (c) $today.year- , Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 *
 *
 * Created by Emmanuel Keeya Lubowa - ekeeya@skycastleauctionhub.com <ekeeya@skycastleauctionhub.com>.
 *
 * This program is not free software.
 *
 * NOTICE: All information contained herein is, and remains the property of Sky Castle Auction Hub ltd. - www.skycastleauctionhub.com
 */

package com.skycastle.auction.entities.users.dto;
import com.skycastle.auction.entities.users.*;
import com.skycastle.auction.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public GenericUserDTO toDTO(User user, Object qrCode){

        GenericUserDTO userDTO = new GenericUserDTO();

        if (qrCode != null){
            userDTO.setQrCode(qrCode.toString());
        }
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setId(user.getId());
        userDTO.setAddress(user.getAddress());
        userDTO.setAccountType(user.getAccountType());
        userDTO.setRole(user.getRole());
        userDTO.setCreatedAt(user.getCreatedAt());
        Utils.ACCOUNT_TYPE accountType = user.getAccountType();
        if (accountType.equals(Utils.ACCOUNT_TYPE.SELLER)){
            Seller seller = (Seller) user;
            userDTO.setName(seller.getName());
            userDTO.setNin(seller.getNin());
            userDTO.setIdentification(seller.getIdentification());
        }else if(accountType.equals(Utils.ACCOUNT_TYPE.BUYER)){
            Buyer buyer = (Buyer) user;
            userDTO.setFirstname(buyer.getFirstname());
            userDTO.setLastname(buyer.getLastname());
            userDTO.setGender(buyer.getGender());
        }
        else if(accountType.equals(Utils.ACCOUNT_TYPE.ADMIN)){
            AdminUser adminUser = (AdminUser) user;
            userDTO.setFirstname(adminUser.getFirstname());
            userDTO.setLastname(adminUser.getLastname());
            userDTO.setDepartment(adminUser.getDepartment());
        }
        return userDTO;
    }
}
