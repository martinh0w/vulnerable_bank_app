package ninja.jira.skeletonkey.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;
import java.util.Date;

import ninja.jira.skeletonkey.app.utility.StatusBuilder;
import ninja.jira.skeletonkey.app.utility.Util;
import ninja.jira.skeletonkey.app.entity.Transaction;
import ninja.jira.skeletonkey.app.entity.ForeignTransaction;
import ninja.jira.skeletonkey.app.entity.ForeignCurrency;
import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.repository.ForeignTransactionRepository;
import ninja.jira.skeletonkey.app.repository.ForeignCurrencyRepository;
import ninja.jira.skeletonkey.app.repository.AccountRepository;

/**
 * This is the controller for class ForeignTransaction.
 * This class is an extension of class Transaction.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/transaction/foreign")
public class ForeignTransactionController {
    @Autowired
    private ForeignCurrencyRepository foreignCurrencyRepository;
    @Autowired
    private ForeignTransactionRepository foreignTransactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * This method adds a foreign transaction to the db
     * @param fromAccount account funds is being transferred from
     * @param toAccount account funds is being transferred to
     * @param amount amount of funds to be transferred
     * @param description details for future reference
     * @param currencyCode identifies the currency to be used, determines the conversion rate used in foreignCurrency table
     * @return new foreign transaction object if successful, error message otherwise
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewForeignTransaction(@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam Double amount, @RequestParam String description, @RequestParam String currencyCode, String date) {
        try{
            //checks whether from and to accounts exists in db
            if(!accountRepository.findById(fromAccount).isPresent() || !accountRepository.findById(toAccount).isPresent()) {
                //returns error message if either accounts are not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //get foreign currency list by currency code from db
            Optional<ForeignCurrency> foreignCurrencyList = foreignCurrencyRepository.findById(currencyCode);
            if (!foreignCurrencyList.isPresent()) {
                //returns error if currency not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Foreign Currency Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Date transactionDate = null;
            if(date == null) {
                //creates a new date format pointing to today's date
                transactionDate = Util.formatNewDate();
            } else {
                transactionDate = Util.convertToDate(date);
            }

            ForeignCurrency foreignCurrency = foreignCurrencyList.get();
            //applies exchange rate to original amount, rounded to 2 decimals
            Double foreignAmount = Util.roundTo2Decimal(foreignCurrency.getExchangeRate() * amount);

            //retrieve both accounts and perform the transaction using final amount
            Account account1 = accountRepository.findById(fromAccount).get();
            Account account2 = accountRepository.findById(toAccount).get();
            Transaction.transfer(account1, account2, foreignAmount);

            //update both accounts in account db
            accountRepository.save(account1);
            accountRepository.save(account2);


            //creates new foreign transaction object
            ForeignTransaction foreignTransaction = new ForeignTransaction(fromAccount, toAccount, amount, transactionDate, description, currencyCode, foreignAmount);
            foreignTransactionRepository.save(foreignTransaction);

            return new ResponseEntity<>(foreignTransaction, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets back a list of all available currency and to transact in
     * @return foreign currency list in json
     */
    @RequestMapping(path="/list")
    public ResponseEntity<?> getExchangeRate () {
        try {
            //default crud method. finds all foreign currencies in db
            return new ResponseEntity<>(foreignCurrencyRepository.findAll(),HttpStatus.OK );
        }
        catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}