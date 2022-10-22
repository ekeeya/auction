package com.oddjobs.auction.services.users;

import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
import com.oddjobs.auction.utils.Utils;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService{

    User save(User user);
    User findByUsername(String username);
    void disableEnableAccount(User user, boolean value);
    void deleteAccount(User user, boolean permanent);
    User findByEmail(String email);
    Page<User> getAllUsers(int pageNo, int size, String sortBy);
    List<User> findByAccountType(Utils.ACCOUNT_TYPE accountType, int pageNo, int size, String sortBy);

}
