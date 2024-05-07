package com.phc.healthcare.dao;

import com.phc.healthcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDAO extends JpaRepository<User, Integer> {

    User findByEmail(String email);

}
