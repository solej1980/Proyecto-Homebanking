package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.ClientLoan;

import java.util.List;

public interface ClientLoanService {
    void save(ClientLoan clientLoan);
    void saveAll(List<ClientLoan> clientLoans);
}
