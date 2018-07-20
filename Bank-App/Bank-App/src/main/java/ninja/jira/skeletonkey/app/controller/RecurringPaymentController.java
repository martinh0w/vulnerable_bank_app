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
import ninja.jira.skeletonkey.app.entity.RecurringPayment;
import ninja.jira.skeletonkey.app.repository.RecurringPaymentRepository;
import ninja.jira.skeletonkey.app.repository.AccountRepository;

/**
 * This is the controller for class RecurringPayment.
 * Payments occur daily/weekly/monthly
 * Transactions are performed automatically on every first day
 * For example, first day of the week, first day of the month
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/recurringpayment")
public class RecurringPaymentController {
    @Autowired
    private RecurringPaymentRepository recurringPaymentRepository;
    @Autowired
    private AccountRepository accountRepository;


    /**
     * This method adds a new recurring payment to the db.
     * @param fromAccount account funds are being transferred from
     * @param toAccount account funds are being transferred to. accounts can belong to same user
     * @param amount amount of funds being transferred
     * @param period occurrence of transfer (daily/weekly/monthly)
     * @param description details for future reference
     * @return
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewRecurringPayment (@RequestParam String fromAccount, @RequestParam String toAccount, @RequestParam Double amount, @RequestParam String period, @RequestParam String description) {
        try{
            //checks if sending and receiving accounts exist
            if(!accountRepository.findById(fromAccount).isPresent() || !accountRepository.findById(toAccount).isPresent()) {
                //returns error message if either accounts are not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //recurring payment is created and returned
            RecurringPayment recurringPayment = new RecurringPayment(fromAccount, toAccount, amount, period, description);
            recurringPaymentRepository.save(recurringPayment);
            return new ResponseEntity<>(recurringPayment, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets a recurring payment by ID
     * @param recurringID identifies the recurring payment
     * @return recurring payment if successful, error message otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getRecurringPayment (@RequestParam Integer recurringID) {
        try{
            Optional<RecurringPayment> recurringPaymentList = recurringPaymentRepository.findById(recurringID);
            if(!recurringPaymentList.isPresent()) {
                //returns error message if no recurring payment matches id
                StatusBuilder statusBuilder = new StatusBuilder("error","No Recurring Payment Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            RecurringPayment recurringPayment = recurringPaymentList.get();
            return new ResponseEntity<>(recurringPayment, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * This method gets a recurring payment by sending account(user)
     * @param fromAccount user's account
     * @return recurring payment list if successful, error message otherwise
     */
    @RequestMapping(path="/account")
    public ResponseEntity<?> getRecurringPaymentByAccount (@RequestParam String fromAccount) {
        try{
            //gets all recurring payment matching user's account. can be null
            List<RecurringPayment> recurringList = recurringPaymentRepository.findReccuringPaymentsByFromAccount(fromAccount);
            if(!recurringList.isEmpty()) {
                //returns recurring payment list if not empty
                return new ResponseEntity<>(recurringList, HttpStatus.OK);
            }
            //returns error message if no account or recurring payment is found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Recurring Payment Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method gets recurring payments categorized by periods
     * @param fromAccount user's account
     * @param period occurrence of transaction (daily/weekly/monthly)
     * @return recurring payment list if successful, error message otherwise
     */
    @RequestMapping(path="/period")
    public ResponseEntity<?> getRecurringPaymentByPeriod (@RequestParam String fromAccount, @RequestParam String period) {
        try{
            //gets recurring payment by account number and period from recurring payment db
            List<RecurringPayment> recurringList = recurringPaymentRepository.findRecurringPaymentsByFromAccountAndPeriod(fromAccount, period);
            if(!recurringList.isEmpty()) {
                //returns recurring payment list if not empty
                return new ResponseEntity<>(recurringList, HttpStatus.OK);
            }
            //returns error message if no recurring payment found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Recurring Payment Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns all recurring payments
     * Does not check for empty list
     * @return recurring payment list if successful, empty json if list is empty
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllRecurringPayments () {
        try{
            //default crud method. returns all recurring payment found in db
            return new ResponseEntity<>(recurringPaymentRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates an existing recurring payment in db
     * @param recurringID identifies the recurring payment (id auto generated by db, id number shared across all tables)
     * @param fromAccount account funds is being transferred from
     * @param toAccount account funds is being transferred to
     * @param amount amount of funds being transferred
     * @param period occurrence of payment (daily/weekly/monthly)
     * @param description details for future reference
     * @return
     */
    @PostMapping(path="/update")
    public ResponseEntity<?> updateRecurringPayment (@RequestParam Integer recurringID, String fromAccount, String toAccount, Double amount, String period, String description) {
        try{
            //gets the recurring payment list by recurring id
            Optional<RecurringPayment> recurringList = recurringPaymentRepository.findById(recurringID);
            if(!recurringList.isPresent()) {
                //returns error message if recurring payment not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Recurring Payment Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            RecurringPayment recurringPayment = recurringList.get();
            //sets specified params if present in request message
            if(fromAccount != null) {
                if(!accountRepository.findById(fromAccount).isPresent()) {
                    //returns error message if account not found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                recurringPayment.setFromAccount(fromAccount);
            }
            if(toAccount != null) {
                if(!accountRepository.findById(toAccount).isPresent()) {
                    //returns error message if account not found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                recurringPayment.setToAccount(toAccount);
            }
            if(amount != null) {
                recurringPayment.setAmount(amount);
            }
            if(period != null) {
                recurringPayment.setPeriod(period);
            }
            if(description != null) {
                recurringPayment.setDescription(description);
            }

            //updates recurring payment in db and returns recurring payment
            recurringPaymentRepository.save(recurringPayment);
            return new ResponseEntity<>(recurringPayment, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method deletes a recurring payment matching recurring id
     * @param recurringID identifies the recurring payment
     * @return success message if successful, error message otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deleteRecurringPayment (@RequestParam Integer recurringID) {
        try{
            Optional<RecurringPayment> recurringList = recurringPaymentRepository.findById(recurringID);
            if(recurringList.isPresent()) {
                recurringPaymentRepository.deleteById(recurringID);
                StatusBuilder statusBuilder = new StatusBuilder("success","Recurring Payment Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            StatusBuilder statusBuilder = new StatusBuilder("error","No Recurring Payment Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes all recurring payments
     * Does not check whether db is empty
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllRecurringPayments () {
        try{
            //default crud method. deletes all recurring payments from db
            recurringPaymentRepository.deleteAll();
            StatusBuilder statusBuilder = new StatusBuilder("success","Recurring Payments Deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}