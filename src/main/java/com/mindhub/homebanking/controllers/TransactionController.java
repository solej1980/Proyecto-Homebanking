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


        Client client = clientService.findByEmail(authentication.getName());
        Account accountFrom = accountService.findByNumber(fromAccountNumber);
        Account accountTo = accountService.findByNumber(toAccountNumber);

        if (fromAccountNumber.isBlank()) {
            return new ResponseEntity<>("Account number is required", HttpStatus.FORBIDDEN);
        } else {
            if (accountFrom == null) {
                return new ResponseEntity<>("The account does not exist", HttpStatus.FORBIDDEN);
            } else if (!client.getAccounts().contains(accountFrom)) {
                return new ResponseEntity<>("Account not found in current client", HttpStatus.FORBIDDEN);
            } else {
                if (toAccountNumber.isBlank()) {
                    return new ResponseEntity<>("Account number is required", HttpStatus.FORBIDDEN);
                } else if (accountTo == null) {
                    return new ResponseEntity<>("The account does not exist", HttpStatus.FORBIDDEN);
                } else {
                    if (amount <= 0) {
                        return new ResponseEntity<>("Mount is required", HttpStatus.FORBIDDEN);
                    } else if (description.isBlank()) {
                        return new ResponseEntity<>("Description is required", HttpStatus.FORBIDDEN);
                    } else if (fromAccountNumber.equals(toAccountNumber)) {
                        return new ResponseEntity<>("Origin and destiny account have to be different", HttpStatus.FORBIDDEN);
                    } else if (accountFrom.getBalance() < amount) {
                        return new ResponseEntity<>("Not enough funds for the transaction", HttpStatus.FORBIDDEN);
                    }
                }
            }
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