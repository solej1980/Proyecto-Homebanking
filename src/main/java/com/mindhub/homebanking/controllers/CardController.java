package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.Random;
import java.util.Set;


@RestController
@RequestMapping("/api")
public class CardController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;


    public String cardNumberGenerator(){
        return String.format("%04d", (short) Math.floor(Math.random() * 9999))
                + "-" + String.format("%04d", (short) Math.floor(Math.random() * 9999))
                + "-" + String.format("%04d", (short) Math.floor(Math.random() * 9999))
                + "-" + String.format("%04d", (short) Math.floor(Math.random() * 9999));
    }

    public short cvvGenerator(){
        Random random = new Random();
        return Short.parseShort(String.format("%03d", random.nextInt(1000)));
    }
    @RequestMapping("/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, CardType cardType, CardColor cardColor){
        String cardNumber;

        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is required.");
        }

        Client client = clientService.findByEmail(authentication.getName());
        if (client != null) {
            Set<Card> cards= client.getCards();
            if (cards.stream().filter(card -> card.getType() == cardType).count() < 3) {
                do {
                    cardNumber = cardNumberGenerator();
                } while (cardService.existsByNumber(cardNumber));

                short cvv = cvvGenerator();
                Card card1 = new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor, cardNumber, cvv, LocalDate.now(), LocalDate.now().plusYears(5));
                client.addCard(card1);
                clientService.save(client);
                cardService.save(card1);
                return new ResponseEntity<>(HttpStatus.CREATED);
            }else{
                return new ResponseEntity<>("You already have three card", HttpStatus.FORBIDDEN);
            }

        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authenticated client.");

        }
    }


}