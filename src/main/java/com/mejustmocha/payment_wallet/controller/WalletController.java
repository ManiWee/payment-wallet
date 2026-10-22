package com.mejustmocha.payment_wallet.controller;

import com.mejustmocha.payment_wallet.dto.AccountResponseDTO;
import com.mejustmocha.payment_wallet.dto.TransferRequest;
import com.mejustmocha.payment_wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @GetMapping("/balance/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getBalance(@PathVariable String accountNumber) {
        AccountResponseDTO response = walletService.getAccountBalance(accountNumber);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(@RequestBody TransferRequest transferRequest) {
        walletService.transferMoney(transferRequest);
        return ResponseEntity.ok("Transfer Successful");
    }
}
