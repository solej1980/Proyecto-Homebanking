package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    AccountService accountService;
    @RequestMapping("/clients")
    public List<ClientDTO> getClients() {
        return clientService.findAll();
    }

    @RequestMapping("/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientService.getClientDTO(id);
    }


    @Autowired
    private PasswordEncoder passwordEncoder;

    public String accountGenerator() {
        int n;
        String number="";
        for (int i=0;i<8;i++) {
            n = (int) ((Math.random() * 10));
            number += n;
        }
        return "VIN-" + number;
    }





    @RequestMapping( path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String firstName,
                                           @RequestParam String lastName,
                                           @RequestParam String email,
                                           @RequestParam String password) {

        if(firstName.isEmpty()) {
            return new ResponseEntity<>("First name is required",HttpStatus.FORBIDDEN);
        } else if (lastName.isEmpty()) {
            return new ResponseEntity<>("Last name is required", HttpStatus.FORBIDDEN);
        } else if (email.isEmpty()) {
            return new ResponseEntity<>("E-mail is required", HttpStatus.FORBIDDEN);
        } else if (password.isEmpty()) {
            return new ResponseEntity<>("Password is required", HttpStatus.FORBIDDEN);
        }


        if (clientService.findByEmail(email) !=  null) {

            return new ResponseEntity<>("Email already in use", HttpStatus.FORBIDDEN);

        }



        Client client = new Client(firstName, lastName, email, passwordEncoder.encode(password));


        clientService.save(client);
        String numberAccount;
        do {
            numberAccount = accountGenerator();
        }while(accountService.existsByNumber(numberAccount));
        Account account = new Account(numberAccount, LocalDate.now(), 0);
        client.addAccount(account);
        accountService.save(account);
        clientService.save(client);
        return new ResponseEntity<>(HttpStatus.CREATED);


    }

    @RequestMapping("/clients/current")
    public ClientDTO getCurrent(Authentication authentication){
        return new ClientDTO(clientService.findByEmail(authentication.getName()));
    }



}
