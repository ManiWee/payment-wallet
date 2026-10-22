package com.mejustmocha.payment_wallet.service;

import com.mejustmocha.payment_wallet.dto.TransferRequest;
import com.mejustmocha.payment_wallet.entity.Account;
import com.mejustmocha.payment_wallet.messaging.NotificationProducer;
import com.mejustmocha.payment_wallet.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private AccountRepository accountRepository; //fake database

    @Mock
    private NotificationProducer notificationProducer; //fake message queue

    @InjectMocks
    private WalletService walletService; //the real service testing in this test class

    private Account sender;
    private Account receiver;

    @BeforeEach
    void setUp() {
        //this runs before every test to set up fresh dummy data
        sender = new Account();
        sender.setAccountNumber("ACC123");
        sender.setBalance(new BigDecimal("500.00"));

        receiver = new Account();
        receiver.setAccountNumber("ACC999");
        receiver.setBalance(new BigDecimal("100.00"));

    }

    @Test
    void transferMoney_SuccessfulTransfer() {
        // 1. Arrange: Prepare the request and program the fake database
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC123");
        request.setToAccountNumber("ACC999");
        request.setAmount(new BigDecimal("200.00"));

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber("ACC999")).thenReturn(Optional.of(receiver));

        // 2. Act: Call the real method
        walletService.transferMoney(request);

        // 3. Assert: Verify the math was done correctly
        assertEquals(new BigDecimal("300.00"), sender.getBalance());
        assertEquals(new BigDecimal("300.00"), receiver.getBalance());

        // Verify the database save and notification were actually triggered
        verify(accountRepository, times(1)).save(sender);
        verify(accountRepository, times(1)).save(receiver);
        verify(notificationProducer, times(1)).sendTransferNotification(anyString());
    }

    @Test
    void transferMoney_ThrowsException_WhenInsufficientFunds() {
        // 1. Arrange: Attempt to transfer $600 from an account with only $500
        TransferRequest request = new TransferRequest();
        request.setFromAccountNumber("ACC123");
        request.setToAccountNumber("ACC999");
        request.setAmount(new BigDecimal("600.00"));

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(sender));
        when(accountRepository.findByAccountNumber("ACC999")).thenReturn(Optional.of(receiver));

        // 2 & 3. Act & Assert: Verify it throws a RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            walletService.transferMoney(request);
        });

        assertEquals("Insufficient funds in sender account", exception.getMessage());

        // Verify the database was NEVER updated
        verify(accountRepository, never()).save(any());
        verify(notificationProducer, never()).sendTransferNotification(anyString());
    }
}
