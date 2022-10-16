package com.oddjobs.auction.controllers.users;

import com.oddjobs.auction.entities.users.AdminUser;
import com.oddjobs.auction.entities.users.Buyer;
import com.oddjobs.auction.entities.users.Seller;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
import com.oddjobs.auction.utils.Utils;
import org.springframework.stereotype.Component;

@Component
public class Mapper {


    public GenericUserDTO toDTO(User user){
        GenericUserDTO userDTO = new GenericUserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setId(user.getId());
        userDTO.setAddress(user.getAddress());
        userDTO.setAccountType(user.getAccountType());
        userDTO.setRole(user.getRole());
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
