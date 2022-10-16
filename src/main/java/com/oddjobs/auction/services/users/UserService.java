package com.oddjobs.auction.services.users;

import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
import com.oddjobs.auction.utils.Utils;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService{
    User findByUsername(String username);

    Page<User> getAllUsers(int pageNo, int size, String sortBy);
    List<User> findByAccountType(Utils.ACCOUNT_TYPE accountType, int pageNo, int size, String sortBy);

}
