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
import java.util.List;

import ninja.jira.skeletonkey.app.utility.StatusBuilder;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.repository.UserRepository;
import ninja.jira.skeletonkey.app.repository.AccountRepository;

/**
 * This is the controller for class Account.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/account")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * This methods adds a new account to the account db
     * @param accountNumber identifies the account
     * @param userID specifies which user this account belongs to
     * @param accountType type of account (savings/checking)
     * @param balance amount of funds in account
     * @param spendingLimit limit of spending per month
     * @param withdrawalLimit limit of withdrawal per month
     * @return account object in json if successful, error message otherwise
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewAccount (@RequestParam String accountNumber, @RequestParam String userID, @RequestParam String accountType, @RequestParam double balance, @RequestParam double spendingLimit, @RequestParam double withdrawalLimit) {
        try {
            //gets the user list from user db. can be null
            Optional<User> userList = userRepository.findById(userID);
            if (!userList.isPresent()) {
                //returns error if no user found
                StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            if(accountRepository.findById(accountNumber).isPresent()) {
                //returns error if an account already exists
                StatusBuilder statusBuilder = new StatusBuilder("error","Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //creates the account if there is a user and no such account exists yet, returns the account object
            Account acc = new Account(accountNumber, userID, accountType, balance, spendingLimit, withdrawalLimit);
            accountRepository.save(acc);
            return new ResponseEntity<>(acc, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method gets the account matching the accountNumber
     * @param accountNumber identifies the account
     * @return account object in json if successful, error message otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getAccount (@RequestParam String accountNumber) {
        try {
            //gets the account list from account db. can be null
            Optional<Account> accList = accountRepository.findById(accountNumber);
            if(accList.isPresent()) {
                //returns the account object if found
                Account acc = accList.get();
                return new ResponseEntity<>(acc, HttpStatus.OK);
            }
            //returns error message if no account found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns all accounts found in account db
     * @return all accounts in json, empty json if no accounts found
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllAccounts () {
        try {
            return new ResponseEntity<>(accountRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }


    }

    /**
     * This method gets all accounts that belong to a user
     * @param userID identifies the user
     * @return all accounts in json, error message otherwise
     */
    @RequestMapping(path="/user")
    public ResponseEntity<?> getAllAccountByUser (@RequestParam String userID) {
        try {
            //finds all accounts that matches a userID. can be empty
            List<Account> accList = accountRepository.findAccountsByUserID(userID);
            if(!accList.isEmpty()) {
                //returns the list if not empty
                return new ResponseEntity<>(accList, HttpStatus.OK);
            }
            //returns error if no accounts found for the user
            StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates the account using param specified in request message
     * @param accountNumber identifies the account
     * @param userID specifies which user the account is tied to
     * @param accountType type of account (savings/checking)
     * @param balance amount of funds in account
     * @param spendingLimit limit of spending per month
     * @param withdrawalLimit limit of withdrawal per month
     * @return updated account object if successful, error message otherwise
     */
    @PostMapping(path="/update")
    public ResponseEntity<?> updateAccount (@RequestParam String accountNumber, String userID, String accountType, Double balance, Double spendingLimit, Double withdrawalLimit) {
        try {
            //gets the account list from account db. can be null
            Optional<Account> accList = accountRepository.findById(accountNumber);
            if(!accList.isPresent()) {
                //returns error if no account is found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Account acc = accList.get();

            //checks whether user exists
            if(userID != null) {
                if(!userRepository.findById(userID).isPresent()) {
                    //returns error if no user found in db
                    StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                acc.setUserID(userID);
            }
            if(accountType != null) {
                acc.setAccountType(accountType);
            }
            if(balance != null) {
                acc.setBalance(balance);
            }
            if(spendingLimit != null) {
                acc.setSpendingLimit(spendingLimit);
            }
            if(withdrawalLimit != null) {
                acc.setWithdrawalLimit(withdrawalLimit);
            }

            //updates the account db , returns updated account object
            accountRepository.save(acc);
            return new ResponseEntity<>(acc, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }


    }

    /**
     * This method deletes a account matching the accountNumber
     * @param accountNumber identifies the account
     * @return success message if successful, error otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deleteAccount(@RequestParam String accountNumber) {
        try {
            //gets the account list matching id from db
            Optional<Account> accList = accountRepository.findById(accountNumber);
            if (accList.isPresent()) {
                accountRepository.deleteById(accountNumber);

                //returns success if account is found and deleted
                StatusBuilder statusBuilder = new StatusBuilder("success","Account Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            //returns error if no account is found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }


    }

    /**
     * This method deletes all accounts in db
     * Does not check whether database is empty
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllAccounts() {
        try {
            //default crud method. deletes all accounts from db
            accountRepository.deleteAll();

            //returns success after deleting all from db
            StatusBuilder statusBuilder = new StatusBuilder("success","Accounts Deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
 }