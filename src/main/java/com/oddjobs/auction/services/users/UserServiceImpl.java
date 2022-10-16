package com.oddjobs.auction.services.users;

import com.oddjobs.auction.controllers.users.Mapper;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.entities.users.dto.GenericUserDTO;
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
    private final Mapper mapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Mapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
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
}
