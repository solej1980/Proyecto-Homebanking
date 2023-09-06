package com.mindhub.homebanking.services;

import com.mindhub.homebanking.models.Card;

import java.util.List;

public interface CardService {
    void save(Card card);
    void saveAll(List<Card> cards);
    List<Card> findAll();
    boolean existsByNumber(String string);
}
