package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.List;
import java.util.Set;

public interface AccountService {

    AccountDTO getAccountDTOByNumber(String number);
    void save(Account account);
    AccountDTO getAccountDTOById(Long id);
    Set<AccountDTO> getAllAccounts();
    Account findByNumber(String number);
    boolean existsByNumber(String number);
}
