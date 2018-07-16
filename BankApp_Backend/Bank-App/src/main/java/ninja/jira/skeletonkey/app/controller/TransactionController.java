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
import java.util.ArrayList;
import java.util.Date;
import java.time.LocalDate;

import ninja.jira.skeletonkey.app.utility.Util;
import ninja.jira.skeletonkey.app.utility.StatusBuilder;
import ninja.jira.skeletonkey.app.entity.Transaction;
import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.repository.TransactionRepository;
import ninja.jira.skeletonkey.app.repository.AccountRepository;

/**
 * This is the controller for class Transaction.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/transaction")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * This method adds a new transaction and updates the balance on the two accounts involved
     * @param fromAccount account funds are being transferred from
     * @param toAccount account funds are being transferred to
     * @param amount amount of funds being transferred
     * @param description details for future reference
     * @return new transaction if successful, error message otherwise
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewTransaction (@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam Double amount, @RequestParam String description, String date) {
        try {

            //checks if sending and receiving accounts exist
            if(!accountRepository.findById(fromAccount).isPresent() || !accountRepository.findById(toAccount).isPresent()) {
                //returns error message if either accounts are not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Date transactionDate = null;
            if(date == null) {
                //creates a new date format pointing to today's date
                transactionDate = Util.formatNewDate();
            } else {
                transactionDate = Util.convertToDate(date);
            }


            //retrieve accounts from db
            Account account1 = accountRepository.findById(fromAccount).get();
            Account account2 = accountRepository.findById(toAccount).get();

            //perform transaction and updates the accounts
            Transaction.transfer(account1, account2, amount);
            accountRepository.save(account1);
            accountRepository.save(account2);

            //creates and returns a new transaction object
            Transaction transaction = new Transaction(fromAccount, toAccount, amount, transactionDate, description);
            transactionRepository.save(transaction);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets the transaction matching a transaction id
     * @param transactionID identifies the transaction
     * @return transaction if successful, error message otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getTransactionByID (@RequestParam Integer transactionID) {
        try{
            //gets the transaction matching transaction id
            Optional<Transaction> transactionList = transactionRepository.findById(transactionID);
            if(transactionList.isPresent()) {
                //returns transaction list if found
                return new ResponseEntity<>(transactionList, HttpStatus.OK);
            }
            //returns error message if no transcation found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Transaction Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns transactions matching an account
     * @param fromAccount identifies the account (user)
     * @return transaction if successful, error message otherwise
     */
    @RequestMapping(path="/account")
    public ResponseEntity<?> getTransactionByFromAccount (@RequestParam String fromAccount) {
        try {
            //get transaction list matching account number from db
            List<Transaction> transactionList = transactionRepository.findTransactionsByFromAccount(fromAccount);
            if (!transactionList.isEmpty()) {
                //returns transaction list if found
                return new ResponseEntity<>(transactionList, HttpStatus.OK);
            }
            //returns error message if no transactions found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Transaction Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns transactions matching a user
     * @param userID identifies a user
     * @return transaction if successful, error message otherwise
     */
    @RequestMapping(path="/user")
    public ResponseEntity<?> getTransactionByUser (@RequestParam String userID) {
        try{
            //finds all accounts that matches a userID. can be empty
            List<Account> accList = accountRepository.findAccountsByUserID(userID);
            List<Transaction> resultList = new ArrayList<>();
            for(int i = 0; i < accList.size(); i++){
                Account acc = accList.get(i);
                List<Transaction> transactionList = transactionRepository.findTransactionsByFromAccount(acc.getAccountNumber());
                for(int j = 0; j < transactionList.size(); j++) {
                    resultList.add(transactionList.get(j));
                }
            }
            if(resultList.isEmpty()) {
                StatusBuilder statusBuilder = new StatusBuilder("error","No Transaction Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(resultList, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets transactions matching to an account funds are being transferred to
     * @param fromAccount account funds are being transferred from
     * @param toAccount account funds are being transferred to
     * @return transaction if successful, error message otherwise
     */
    @RequestMapping(path="/payee")
    public ResponseEntity<?> getTransactionByToAccount (@RequestParam String fromAccount, @RequestParam String toAccount) {
        try {
            //gets all transaction matching account numbers
            List<Transaction> transactionList = transactionRepository.findTransactionsByFromAccountAndToAccount(fromAccount, toAccount);
            if (!transactionList.isEmpty()) {
                //returns transaction list if found
                return new ResponseEntity<>(transactionList, HttpStatus.OK);
            }
            //returns error message if transactions not found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Transaction Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns transactions that occurred within a date range for a particular account
     * @param fromAccount account funds are being transferred from
     * @param fromDate starting date in format (yyyy-MM-dd)
     * @param toDate ending date in format (yyyy-MM-dd)
     * @return transaction list if successful, error message otherwise
     */
    @RequestMapping(path="/date")
    public ResponseEntity<?> getTransactionByDate (@RequestParam String fromAccount, @RequestParam String fromDate, @RequestParam String toDate) {
        try {

            if(!accountRepository.findById(fromAccount).isPresent()) {
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //convert string dates to localdate format
            LocalDate start = LocalDate.parse(fromDate);
            LocalDate end = LocalDate.parse(toDate);

            //checks if ending date is before start date
            if(end.isBefore(start)) {
                //returns error message if ending date is before start date
                StatusBuilder statusBuilder = new StatusBuilder("error","End Date is before Start Date");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            List<Transaction> transactionList = new ArrayList<>();
            //iterate through all dates given in the range
            for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
                //converts date format to string to query from db
                Date transacDate = Util.convertToDate(date.toString());

                //default crud method. finds all transactions matching account number and transaction date
                List<Transaction> dateList = transactionRepository.findTransactionsByFromAccountAndDate (fromAccount, transacDate);
                //adds all results found to list
                if(!dateList.isEmpty()) {
                    for (Transaction item : dateList) {
                        transactionList.add(item);
                    }
                }
            }

            //return transaction list of all matching transactions
            return new ResponseEntity<>(transactionList, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns all transactions from the db
     * Does not check if db is empty
     * @return transaction list if successful, empty json if db is empty
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllTransactions () {
        try {
            //default crud method. finds all transactions in db
            return new ResponseEntity<>(transactionRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates an existing transaction
     * @param transactionID identifies the transaction (id auto generated by db, id number shared across all tables)
     * @param fromAccount account funds are being transferred from
     * @param toAccount account funds are being transferred to
     * @param amount amount of funds being transferred
     * @param date date of transaction in format (yyyy-MM-dd)
     * @param description details for future reference
     * @return updated transaction list if successful, error message otherwise
     */
    @PostMapping(path="/update")
    public ResponseEntity<?> updateTransaction (@RequestParam Integer transactionID, String fromAccount, String toAccount, Double amount, String date, String description) {
        try {
            //gets transaction list matching transaction id
            Optional<Transaction> transactionList = transactionRepository.findById(transactionID);
            if (!transactionList.isPresent()) {
                //returns error message if no transaction found
                StatusBuilder statusBuilder = new StatusBuilder("error", "No Transaction Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Transaction transaction = transactionList.get();
            //sets specified param if present in request message
            if (fromAccount != null) {
                //checks if account exists
                if(!accountRepository.findById(fromAccount).isPresent()) {
                    //returns error message if account not found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                transaction.setFromAccount(fromAccount);
            }
            if (toAccount != null) {
                //checks if account exists
                if(!accountRepository.findById(toAccount).isPresent()) {
                    //returns error message if not found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                transaction.setToAccount(toAccount);
            }
            if (amount != null) {
                transaction.setAmount(amount);
            }
            if (date != null) {
                transaction.setDate(Util.convertToDate(date));
            }
            if (description != null) {
                transaction.setDescription(description);
            }

            //updates and returns transaction
            transactionRepository.save(transaction);
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes a transaction matching transaction id
     * @param transactionID identifies the transaction
     * @return success message if successful, error message otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deleteTransaction (@RequestParam Integer transactionID) {
        try {
            //gets transaction list matching transaction id
            Optional<Transaction> transactionList = transactionRepository.findById(transactionID);
            if(transactionList.isPresent()) {
                transactionRepository.deleteById(transactionID);
                //deletes transaction if found, returns success message
                StatusBuilder statusBuilder = new StatusBuilder("success","Transaction Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            //returns error message if no transaction found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Transaction Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes all transactions in db
     * Does not check if db is empty
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllTransactions () {
        try {
            //default crud method. deletes all transactions from the db
            transactionRepository.deleteAll();
            StatusBuilder statusBuilder = new StatusBuilder("success","Transactions Deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}