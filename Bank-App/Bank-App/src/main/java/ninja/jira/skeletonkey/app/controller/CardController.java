package ninja.jira.skeletonkey.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.List;

import ninja.jira.skeletonkey.app.utility.StatusBuilder;
import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.Card;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.CardRepository;

/**
 * This is the controller for class Card.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/card")
public class CardController {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * This method adds a new card to the db
     * @param cardNumber identifies the card
     * @param accountNumber specifies which account the card is tied to
     * @param PIN authenticates the card user
     * @param cardType type of card (debit/credit)
     * @param spendingLimit limit of spending per month
     * @return
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewCard (@RequestParam String cardNumber, @RequestParam String accountNumber, @RequestParam Integer PIN, @RequestParam String cardType, @RequestParam Double spendingLimit) {
        try{
            //gets card list matching id from db
            Optional<Card> cardList = cardRepository.findById(cardNumber);
            if(cardList.isPresent()) {
                //returns error if card not found
                StatusBuilder statusBuilder = new StatusBuilder("error","Card Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //returns error if account not found
            if(!accountRepository.findById(accountNumber).isPresent()) {
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //creates new card if card does not exist yet and an account exists, returns card object
            Card card = new Card(cardNumber, accountNumber, PIN, cardType, spendingLimit);
            cardRepository.save(card);
            return new ResponseEntity<>(card, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets the card matching the cardNumber
     * @param cardNumber identifies the card
     * @return card object if successful, error otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getCard(@RequestParam String cardNumber) {
        try{
            //gets the card list by id. can be null
            Optional<Card> cardList = cardRepository.findById(cardNumber);
            if(cardList.isPresent()) {
                Card card = cardList.get();

                //returns the card object if found
                return new ResponseEntity<>(card, HttpStatus.OK);
            }

            //returs error message if card is not found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Card Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method retrieves all cards from db
     * @return card objects in json, empty json if no cards in db
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllCards() {
        try{
            return new ResponseEntity<>(cardRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets all cards tied to an account
     * @param accountNumber identifies an account
     * @return card objects in json, error message otherwise
     */
    @RequestMapping(path="/account")
    public ResponseEntity<?> getAllCardsByAccount (@RequestParam String accountNumber) {
        try{
            //gets all cards that match the account number. can be empty
            List<Card> cardList = cardRepository.findCardsByAccountNumber(accountNumber);
            if(!cardList.isEmpty()) {
                //return the list if not empty
                return new ResponseEntity<>(cardList, HttpStatus.OK);
            }

            //returns error if there is no card or account does not exist
            StatusBuilder statusBuilder = new StatusBuilder("error","No Card Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates the card object by the params specified in request message
     * @param cardNumber identifies the card
     * @param accountNumber specifies the account tied to the card
     * @param PIN authenticates the card user
     * @param cardType type of card (debit/credit)
     * @param spendingLimit limit of spending per month
     * @return updated card object if successful, error message otherwise
     */
    @PostMapping(path="/update")
    public ResponseEntity<?> updateCard (@RequestParam String cardNumber, String accountNumber, Integer PIN, String cardType, Double spendingLimit){
        try{
            //gets the card list by id. can be null
            Optional<Card> cardList = cardRepository.findById(cardNumber);
            if(!cardList.isPresent()) {
                //returns error message if no cards found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Card Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Card card = cardList.get();

            //checks account if account specified in request param
            if(accountNumber != null) {
                if(!accountRepository.findById(accountNumber).isPresent()) {
                    //returns error message if no account found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                card.setAccountNumber(accountNumber);
            }
            if(PIN != null) {
                card.setPIN(PIN);
            }
            if(cardType != null) {
                card.setCardType(cardType);
            }
            if(spendingLimit != null) {
                card.setSpendingLimit(spendingLimit);
            }

            //update card object in card db, returns updated card object
            cardRepository.save(card);
            return new ResponseEntity<>(card, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes a card matching the cardNumber
     * @param cardNumber identifies the card
     * @return success message if card deleted, error message otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deleteCard(@RequestParam String cardNumber) {
        try{
            //gets card list by id. can be null
            Optional<Card> cardList = cardRepository.findById(cardNumber);
            if(cardList.isPresent()) {
                cardRepository.deleteById(cardNumber);

                //returns success message if card is found and deleted
                StatusBuilder statusBuilder = new StatusBuilder("success","Card Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            //returns error message if card is not found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Card Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes all card objects in the db
     * Does not check whether db is empty
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllCards() {
        try{
            //default crud method. deletes all cards in db
            cardRepository.deleteAll();
            StatusBuilder statusBuilder = new StatusBuilder("success","Cards Deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}