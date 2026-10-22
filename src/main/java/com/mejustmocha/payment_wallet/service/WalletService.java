package com.mejustmocha.payment_wallet.service;

import com.mejustmocha.payment_wallet.dto.AccountResponseDTO;
import com.mejustmocha.payment_wallet.dto.TransferRequest;
import com.mejustmocha.payment_wallet.entity.Account;
import com.mejustmocha.payment_wallet.repository.AccountRepository;
import jakarta.transaction.Transactional;
import com.mejustmocha.payment_wallet.messaging.NotificationProducer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletService {
    private final AccountRepository accountRepository;
    private final NotificationProducer notificationProducer;

    public WalletService(AccountRepository accountRepository, NotificationProducer notificationProducer) {
        this.accountRepository = accountRepository;
        this.notificationProducer = notificationProducer;
    }

    //Read operation with Redis caching
    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountResponseDTO getAccountBalance(String accountNumber){
        System.out.println("Fetching from PostgreSQL database..."); // for verification
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " +accountNumber));

        return new AccountResponseDTO(account.getAccountNumber(), account.getBalance());
    }

    //Automatic money transfer operation
    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public void transferMoney(TransferRequest request){
        if(request.getAmount().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Account sender = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account receiver = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if(sender.getBalance().compareTo(request.getAmount()) < 0){
            throw new RuntimeException("Insufficient balance in sender account");
        }

        //Perform balance updates
        sender.setBalance(sender.getBalance().subtract(request.getAmount()));
        receiver.setBalance(receiver.getBalance().add(request.getAmount()));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        String notificationMsg = String.format("Transfer of $%s from %s to %s was successfully.",
                request.getAmount(), request.getFromAccountNumber(), request.getToAccountNumber());

        notificationProducer.sendTransferNotification(notificationMsg);
    }
}
