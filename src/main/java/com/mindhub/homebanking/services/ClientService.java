package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    void save(Client client);
    void saveAll(List<Client> clients);
    List<ClientDTO> findAll();
    ClientDTO getClientDTO(Long id);
    Client findByEmail(String email);
    Client findById(Long id);
}
