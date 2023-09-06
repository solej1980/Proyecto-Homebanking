package com.mindhub.homebanking.services.implement;

import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CardServiceImplement implements CardService {
    @Autowired
    CardRepository cardRepository;

    @Override
    public void save(Card card) {
        cardRepository.save(card);

    }

    @Override
    public void saveAll(List<Card> cards) {
        for(Card card: cards){
            this.save(card);
        }

    }

    @Override
    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    @Override
    public boolean existsByNumber(String number) {
        return cardRepository.existsByNumber(number);
    }
}
