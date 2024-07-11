package com.skycastle.auction.services.users;
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

import com.skycastle.auction.entities.users.*;
import com.skycastle.auction.entities.users.forms.RegisterForm;
import com.skycastle.auction.repositories.UserRepository;
import com.skycastle.auction.utils.Utils;
import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private  final SecretGenerator secretGenerator;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username);
        if (user == null){
            log.error("User not found");
            throw new UsernameNotFoundException(username);
        }
        return user;
    }


    @Override
    public User save(User user){
        return userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    @Override
    public Page<User> getAllUsers(int pageNo, int size, String sortBy) {
        Pageable paging = PageRequest.of(pageNo, size, Sort.by(sortBy).ascending());
        return userRepository.findAll(paging);
    }

    @Override
    public List<User> findByAccountType(Utils.ACCOUNT_TYPE accountType, int pageNo, int size, String sortBy) {
        return userRepository.findByAccountType(accountType);
    }

    @Override
    public void disableEnableAccount(User user, boolean value){
        user.setEnabled(value);
        user.setStatus(value ? Utils.ACCOUNT_STATUS.ACTIVE : Utils.ACCOUNT_STATUS.DISABLED);
        userRepository.save(user);
    }
    @Override
    public void deleteAccount(User user, boolean permanent){
       if (permanent){

           userRepository.delete(user);
       }else{
           user.setDeleted(true);
           userRepository.save(user);
       }
    }

    @Override
    public User registerUser(RegisterForm registerForm) throws Exception {

        User user;
        user = findByEmail(registerForm.getEmail());
        if (user !=null){
            throw new Exception("Email already exists");
        }

        user = findByUsername(registerForm.getUsername());

        if(user != null){
            throw new Exception("Username already exists");
        }

        String accountType = registerForm.getAccountType();
        Utils.ROLES role;
        if (accountType.equals(Utils.ACCOUNT_TYPE.SELLER.name())) {
            user = new Seller();
            ((Seller) user).setIdentification(registerForm.getIdentification());
            ((Seller) user).setName(registerForm.getName());
            ((Seller) user).setNin(registerForm.getNin());
            role = Utils.ROLES.ROLE_SELLER;
        } else if (accountType.equals(Utils.ACCOUNT_TYPE.BUYER.name())) {
            user = new Buyer();
            ((Buyer) user).setFirstname(registerForm.getFirstname());
            ((Buyer) user).setLastname(registerForm.getLastname());
            ((Buyer) user).setGender(registerForm.getGender());
            role = Utils.ROLES.ROLE_BUYER;

        } else if(accountType.equals(Utils.ACCOUNT_TYPE.ADMIN.name())) {
            user = new AdminUser();
            ((AdminUser) user).setFirstname(registerForm.getFirstname());
            ((AdminUser) user).setLastname(registerForm.getLastname());
            ((AdminUser) user).setDepartment(registerForm.getDepartment());
            role = Utils.ROLES.ROLE_ADMIN;
        }else{
            user =  new Anonymous();
            role = Utils.ROLES.ROLE_ANONYMOUS;
        }
        user.setUsername(registerForm.getUsername());
        user.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        user.setAccountType(Utils.ACCOUNT_TYPE.valueOf(accountType));
        user.setEmail(registerForm.getEmail());
        user.setAddress(registerForm.getAddress());
        user.setRole(role);
        if (registerForm.isEnable2Fa()) {
            user.setUsing2FA(true);
            user.setSecret(secretGenerator.generate());
        }
        return save(user);
    }
}
