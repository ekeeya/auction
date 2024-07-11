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

package com.skycastle.auction.services.users;

import com.skycastle.auction.entities.users.User;
import com.skycastle.auction.entities.users.forms.RegisterForm;
import com.skycastle.auction.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User save(User user);
    User findByUsername(String username);
    User findUserById(Long id);
    void disableEnableAccount(User user, boolean value);
    void deleteAccount(User user, boolean permanent);

    User registerUser(RegisterForm registerForm) throws  Exception;
    User findByEmail(String email);
    Page<User> getAllUsers(int pageNo, int size, String sortBy);
    List<User> findByAccountType(Utils.ACCOUNT_TYPE accountType, int pageNo, int size, String sortBy);

}
