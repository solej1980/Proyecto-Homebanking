package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/api")

public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository;
    @RequestMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(Collectors.toSet());
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> new AccountDTO(account)).orElse(null);
    }


    public String accountGenerator() {
        int n;
        String number="";
        for (int i=0;i<8;i++) {
            n = (int) ((Math.random() * 10));
            number += n;
        }
        return "VIN-" + number;
    }




    @RequestMapping(path = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> createAccount(Authentication authentication){
        String numberAccount;
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client.getAccounts().size()<3){
            do {
                numberAccount = accountGenerator();

            }while(accountRepository.existsByNumber(numberAccount));
            Account account = new Account(numberAccount, LocalDate.now(), 0);
            client.addAccount(account);
            accountRepository.save(account);
            clientRepository.save(client);
            return new ResponseEntity<>("account created",HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("You already have 3 accounts", HttpStatus.FORBIDDEN);
            }
    }
    @RequestMapping("/clients/current/accounts")
    public List<AccountDTO> getAccountsCurrentClient(Authentication authentication) {
        Client currentClient = clientRepository.findByEmail(authentication.getName());
        return currentClient.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }





}
