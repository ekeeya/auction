/*
 * Online Auction web system application
 *
 * Copyright (C) 2021 - , Oddjobs.tech
 *
 * Dev <dev@auction.io>
 *
 * This program is not free software, copying and distribution of this source code is prohibited.
 */
package com.oddjobs.auction.entities.users.dto;
import com.oddjobs.auction.entities.users.AdminUser;
import com.oddjobs.auction.entities.users.Buyer;
import com.oddjobs.auction.entities.users.Seller;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.utils.Utils;
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
        else{
            AdminUser adminUser = (AdminUser) user;
            userDTO.setFirstname(adminUser.getFirstname());
            userDTO.setLastname(adminUser.getLastname());
            userDTO.setDepartment(adminUser.getDepartment());
        }
        return userDTO;
    }
}
