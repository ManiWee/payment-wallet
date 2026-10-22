package com.mejustmocha.payment_wallet.repository;

import com.mejustmocha.payment_wallet.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Spring generates: "SELECT * FROM accounts WHERE account_number = ?"
    Optional<Account> findByAccountNumber(String accountNumber);
}