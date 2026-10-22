package com.mejustmocha.payment_wallet.dto;

import java.math.BigDecimal;

public class AccountResponseDTO {
    private String accountNumber;
    private BigDecimal balance;

    public AccountResponseDTO(String accountNumber, BigDecimal balance){
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }
}
