package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;




@RestController
@RequestMapping("/api")

public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private ClientService clientService;
    @RequestMapping("/accounts")
    public Set<AccountDTO> getAccounts() {
        return accountService.getAllAccounts();
    }

    @RequestMapping("/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id) {
        return accountService.getAccountDTOById(id);
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
        Client client = clientService.findByEmail(authentication.getName());
        if (client.getAccounts().size()<3){
            do {
                numberAccount = accountGenerator();

            }while(accountService.existsByNumber(numberAccount));
            Account account = new Account(numberAccount, LocalDate.now(), 0);
            client.addAccount(account);
            accountService.save(account);
            clientService.save(client);
            return new ResponseEntity<>("account created",HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>("You already have 3 accounts", HttpStatus.FORBIDDEN);
        }
    }
    @RequestMapping("/clients/current/accounts")
    public List<AccountDTO> getAccountsCurrentClient(Authentication authentication) {
        Client currentClient = clientService.findByEmail(authentication.getName());
        return currentClient.getAccounts().stream().map(account -> new AccountDTO(account)).collect(Collectors.toList());
    }





}