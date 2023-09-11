package com.mindhub.homebanking.controllers;


import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;

import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientLoanService clientLoanService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ClientService clientService;

    public LoanController() {
    }


    @RequestMapping("/loans")
    public List<LoanDTO> getLoans() {
        return loanService.getLoansDTO();
    }

    @Transactional
    @RequestMapping(path = "/loans", method = RequestMethod.POST)
    public ResponseEntity<Object> createLoan(@RequestBody LoanApplicationDTO loanApplicationDTO, Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is required.");
        }

        if(loanApplicationDTO.getToAccountNumber().isEmpty()) {
            return new ResponseEntity<>("Number account is required", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getAmount() <= 0) {
            return new ResponseEntity<>("The amount can't be less than or equal to zero", HttpStatus.FORBIDDEN);
        }
        if(loanApplicationDTO.getPayments() == 0) {
            return new ResponseEntity<>("Payments can't be zero", HttpStatus.FORBIDDEN);
        }
        List<Loan> loans = loanService.getLoans();

        if (loans.stream().noneMatch(loan -> loan.getId() == loanApplicationDTO.getLoanId())) {
            return new ResponseEntity<>("Loan type doesn't exist", HttpStatus.FORBIDDEN);
        }

        Loan loan = loanService.findById(loanApplicationDTO.getLoanId());
        if ( loanApplicationDTO.getAmount() > loan.getMaxAmount()){
            return new ResponseEntity<>("Cannot exceed loan max amount", HttpStatus.FORBIDDEN);
        }

        Account account = accountService.findByNumber(loanApplicationDTO.getToAccountNumber());





        ClientLoan clientLoan = new ClientLoan((int)(loanApplicationDTO.getAmount() * 1.2), loanApplicationDTO.getPayments());
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), "" + loan.getName() + " - loan approved", LocalDateTime.now());
        account.setBalance(account.getBalance()+loanApplicationDTO.getAmount());
        account.addTransaction(transaction);

        Client client = clientService.findByEmail(authentication.getName());
        client.addClientLoan(clientLoan);
        loan.addClientLoan(clientLoan);
        clientLoanService.save(clientLoan);
        transactionService.save(transaction);
        loanService.save(loan);
        clientService.save(client);



        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}