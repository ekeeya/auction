package com.oddjobs.auction.services.users;

import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SystemUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public SystemUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       User user = userRepository.findByUsername(username);
       if (user == null){
           throw new UsernameNotFoundException(username);
       }

       return new SystemUserDetails(user);
    }
}
