package com.edwin.trial_bank_app.repository;

import com.edwin.trial_bank_app.dto.AccountType;
import com.edwin.trial_bank_app.entity.Account;
import com.edwin.trial_bank_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface accountRepository extends JpaRepository<Account,Long> {
    Boolean existsByUserAndAccountType(User user, AccountType accountType);
    Boolean existsByAccountNumber(String accountNumber);
    Account findByAccountNumber(String accountNumber);
    Account findByUser(User user);
}
