package com.oddjobs.auction.repositories;

import com.oddjobs.auction.entities.users.User;
import com.oddjobs.auction.utils.Utils;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findAll();
    List<User> findByAccountType(Utils.ACCOUNT_TYPE accountType);
}
