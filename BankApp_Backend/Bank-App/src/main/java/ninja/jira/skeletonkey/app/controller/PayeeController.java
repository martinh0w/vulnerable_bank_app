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
import ninja.jira.skeletonkey.app.entity.Payee;
import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.repository.UserRepository;
import ninja.jira.skeletonkey.app.repository.PayeeRepository;
import ninja.jira.skeletonkey.app.repository.AccountRepository;


/**
 * This is the controller for class Payee.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="api/payee")
public class PayeeController{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PayeeRepository payeeRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * This method adds a new payee to the db
     * @param accountNumber identifies the account of the payee
     * @param userID user adding the payee, tied to a user
     * @param bank bank of the payee
     * @param fullName name of the payee
     * @param initials for easier reference
     * @return payee object if successful, error message otherwise
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewPayee (@RequestParam String accountNumber, @RequestParam String userID, @RequestParam String bank, @RequestParam String fullName, @RequestParam String initials) {
        try{
            //gets user list matching user id
            Optional<User> userList = userRepository.findById(userID);
            if(!userList.isPresent()) {
                //returns error message if user not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            if(!accountRepository.findById(accountNumber).isPresent()) {
                //returns error message if account not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //checks whether accounts belong to different user
            Account account = accountRepository.findById(accountNumber).get();
            if(userList.get().getUserID() == account.getUserID()) {
                //returns error message if account found
                StatusBuilder statusBuilder = new StatusBuilder("error","Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            //checks if payee is already in db
            List<Payee> payeeList = payeeRepository.findPayeesByUserIDAndAccountNumber(userID, accountNumber);
            if (!payeeList.isEmpty()) {
                //returns error message if payee is found
                StatusBuilder statusBuilder = new StatusBuilder("error","Payee Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //creates new payee, returns payee object
            Payee payee = new Payee(accountNumber, userID, bank, fullName, initials);
            payeeRepository.save(payee);
            return new ResponseEntity<>(payee, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * This method returns payee object matching a user and payee's account number
     * @param userID identifies a user
     * @param accountNumber identifies payee's account
     * @return payee object if successful, error otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getPayee (@RequestParam String userID, @RequestParam String accountNumber) {
        try{
            //gets payee list from payee db matching user id and account number
            List<Payee> payeeList = payeeRepository.findPayeesByUserIDAndAccountNumber(userID, accountNumber);
            if (!payeeList.isEmpty()) {
                return new ResponseEntity<>(payeeList, HttpStatus.OK);
            }
            //returns error message if no payee found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Payee Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns all payee from payee db
     * Does not check if list is empty
     * @return payee list
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllAccounts () {
        try{
            //default crud methods. finds all payee in db
            return new ResponseEntity<>(payeeRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets all payees belonging to a user
     * @param userID identifies the user
     * @return payee list if successful, error message otherwise
     */
    @RequestMapping(path="/user")
    public ResponseEntity<?> getAllPayeesByUser (@RequestParam String userID) {
        try{
            //gets payee list matching user id from db
            List<Payee> payeeList = payeeRepository.findByUserID(userID);
            if(!payeeList.isEmpty()) {
                return new ResponseEntity<>(payeeList, HttpStatus.OK);
            }
            //returns error message if no payee found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Payee Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates the payee object in payee db
     * @param payeeID identifies the payee(id auto generated by db, id number shared across all tables)
     * @param accountNumber identifies the payee's account
     * @param userID identifies the user
     * @param bank payee's bank
     * @param fullName payee's full name
     * @param initials payee's initial
     * @return updated payee object if successful, error message otherwise
     */
    @PostMapping(path="/update")
    public ResponseEntity<?> updatePayee (@RequestParam Integer payeeID, String accountNumber, String userID, String bank, String fullName, String initials) {
        try{
            //gets payee list by payee id from db
            Optional<Payee> payeeList = payeeRepository.findById(payeeID);
            if(!payeeList.isPresent()) {
                StatusBuilder statusBuilder = new StatusBuilder("error","No Payee Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Payee payee = payeeList.get();
            //sets specified param if present in request message
            if(accountNumber != null) {
                if(!accountRepository.findById(accountNumber).isPresent()) {
                    //returns error message if no account found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                payee.setAccountNumber(accountNumber);
            }
            if(userID != null) {
                if(!userRepository.findById(userID).isPresent()) {
                    //returns error message if no user found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                payee.setUserID(userID);
            }
            if(bank != null ) {
                payee.setBank(bank);
            }
            if(fullName != null ) {
                payee.setFullName(fullName);
            }
            if(initials != null ) {
                payee.setInitials(initials);
            }

            //updates the payee object in db and returns the payee object
            payeeRepository.save(payee);
            return new ResponseEntity<>(payee, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method deletes a payee matching payee id
     * @param payeeID identifies the payee
     * @return success message if successful, error message otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deletePayee(@RequestParam Integer payeeID) {
        try{
            //gets the payee list by payee id from db
            Optional<Payee> payeeList = payeeRepository.findById(payeeID);
            if (payeeList.isPresent()) {
                payeeRepository.deleteById(payeeID);
                //deletes the payee if found and returns success message
                StatusBuilder statusBuilder = new StatusBuilder("success","Payee Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            //returns error message if payee is not found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Payee Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes all payees in the payee db
     * Does not check if db is empty
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllAccounts() {
        try{
            //default crud method. deletes all payees from db
            payeeRepository.deleteAll();
            StatusBuilder statusBuilder = new StatusBuilder("success","Payees Deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}