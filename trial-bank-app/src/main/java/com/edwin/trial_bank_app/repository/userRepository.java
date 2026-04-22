package com.edwin.trial_bank_app.repository;

import com.edwin.trial_bank_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface userRepository extends JpaRepository<User, Long>{
    Boolean existsByEmail(String email);
    User findByEmail(String email);
    //Boolean existsByPhoneNumber(String phoneNumber);
}