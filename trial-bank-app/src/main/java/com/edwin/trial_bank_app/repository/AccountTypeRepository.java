package com.edwin.trial_bank_app.repository;

import com.edwin.trial_bank_app.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountType, Long> {
    Optional<AccountType> findByTypeNameIgnoreCase(String typeName);
}
