package com.edwin.trial_bank_app.repository;

import com.edwin.trial_bank_app.entity.AccountType;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    Boolean existsByUserAndAccountType(User user, AccountType accountType);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByUser(User user);
}
