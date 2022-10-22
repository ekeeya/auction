package com.oddjobs.auction.services.users;

import com.oddjobs.auction.entities.users.dto.Mapper;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.repositories.UserRepository;
import com.oddjobs.auction.services.base.BaseServiceImpl;
import com.oddjobs.auction.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class UserServiceImpl extends BaseServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
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
        if (permanent)
       if (permanent){
           userRepository.delete(user);
       }
    }
}
