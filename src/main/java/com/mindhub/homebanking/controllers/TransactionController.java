package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;


@RestController
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @Transactional
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity<Object> createTransfer(@RequestParam String fromAccountNumber,
                                                 @RequestParam String toAccountNumber,
                                                 @RequestParam Double amount,
                                                 @RequestParam String description,
                                                 Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is required.");
        }
        Client client = clientService.findByEmail(authentication.getName());

        if (fromAccountNumber.isBlank()) {
            return new ResponseEntity<>("Origin account is required", HttpStatus.FORBIDDEN);
        }
        if (toAccountNumber.isBlank()) {
            return new ResponseEntity<>("Destination account is required", HttpStatus.FORBIDDEN);
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            return new ResponseEntity<>("Origin and destiny account have to be different", HttpStatus.FORBIDDEN);
        }
        if (amount.isNaN()) {
            return new ResponseEntity<>("The amount is required", HttpStatus.FORBIDDEN);
        }
        if (description.isBlank()) {
            return new ResponseEntity<>("Description is required", HttpStatus.FORBIDDEN);
        }
        Account accountFrom = accountService.findByNumber(fromAccountNumber);
        if (accountFrom == null) {
            return new ResponseEntity<>("Origin account not found", HttpStatus.FORBIDDEN);
        }

        Account accountTo = accountService.findByNumber(toAccountNumber);
        if (accountTo == null) {
            return new ResponseEntity<>("Destination account not found", HttpStatus.FORBIDDEN);
        }

        if (!accountFrom.getClient().getEmail().equals(authentication.getName())) {
            return new ResponseEntity<>("The origin account doesn't belong to you", HttpStatus.FORBIDDEN);
        }
        if (amount <= 0) {
            return new ResponseEntity<>("The amount can't be less than or equal to zero", HttpStatus.FORBIDDEN);
        }
        if (accountFrom.getBalance() < amount) {
            return new ResponseEntity<>("Insufficient funds", HttpStatus.FORBIDDEN);
        }
        Transaction transactionDebit = new Transaction(TransactionType.DEBIT, amount, description + " " + toAccountNumber, LocalDateTime.now());
        Transaction transactionCredit = new Transaction(TransactionType.CREDIT, amount, description + " " + fromAccountNumber, LocalDateTime.now());

        accountFrom.setBalance(accountFrom.getBalance() - amount);
        accountTo.setBalance(accountTo.getBalance() + amount);

        accountFrom.addTransaction(transactionDebit);
        accountTo.addTransaction(transactionCredit);

        transactionService.save(transactionDebit);
        transactionService.save(transactionCredit);

        return new ResponseEntity<>("Success transaction", HttpStatus.CREATED);


    }
}