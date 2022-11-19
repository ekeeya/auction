package com.oddjobs.auction.services.users;

import com.oddjobs.auction.entities.users.dto.Mapper;
import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.repositories.UserRepository;
import com.oddjobs.auction.services.base.BaseEntityRepositoryImpl;
import com.oddjobs.auction.utils.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends BaseEntityRepositoryImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
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
        return (User) loadUserByUsername(username);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.getById(id);
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
       if (permanent){

           userRepository.delete(user);
       }else{
           user.setDeleted(true);
           userRepository.save(user);
       }
    }
}
