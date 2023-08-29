package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    CardRepository cardRepository;


    public String cardNumberGenerator(){
        return String.format("%04d", (short) Math.floor(Math.random() * 9999))
                + " " + String.format("%04d", (short) Math.floor(Math.random() * 9999))
                + " " + String.format("%04d", (short) Math.floor(Math.random() * 9999))
                + " " + String.format("%04d", (short) Math.floor(Math.random() * 9999));
    }

    public short cvvGenerator(){
        return Short.parseShort(String.format("%03d", (short) Math.floor(Math.random() * 999)));
    }
    @RequestMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, CardType cardType, CardColor cardColor){
        String cardNumber;
        Client client = clientRepository.findByEmail(authentication.getName());
        if (client.getCards().stream().filter(card -> card.getType() == cardType).count() < 3) {
            do {
                cardNumber = cardNumberGenerator();
            }while(cardRepository.existsByNumber(cardNumber));

            short cvv = cvvGenerator();
            Card card1 = new Card(client.getFirstName()+" "+client.getLastName(), cardType, cardColor , cardNumber, cvv, LocalDate.now(), LocalDate.now().plusYears(5));
            client.addCard(card1);
            clientRepository.save(client);
            cardRepository.save(card1);
            return new ResponseEntity<>(HttpStatus.CREATED);

        }else{
            return new ResponseEntity<>("You already have three card", HttpStatus.FORBIDDEN);
        }
    }


}
