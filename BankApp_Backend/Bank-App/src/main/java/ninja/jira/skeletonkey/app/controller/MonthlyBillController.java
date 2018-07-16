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
import ninja.jira.skeletonkey.app.entity.BillingOrganization;
import ninja.jira.skeletonkey.app.entity.Card;
import ninja.jira.skeletonkey.app.entity.MonthlyBill;
import ninja.jira.skeletonkey.app.repository.MonthlyBillRepository;
import ninja.jira.skeletonkey.app.repository.BillingOrganizationRepository;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.CardRepository;

/**
 * This is the controller for class MonthlyBill.
 * Billing organizations list are pre-generated and do not hold accounts for simplicity.
 * Transactions performed only credits from sending account.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/monthlybill")
public class MonthlyBillController {
    @Autowired
    private BillingOrganizationRepository billingOrganizationRepository;
    @Autowired
    private MonthlyBillRepository monthlyBillRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;

    /**
     * This method adds a new monthly bill to the db
     * @param cardNumber identifies the card to be used to pay for bills
     * @param PIN authenticates the card user
     * @param billingOrganization organization to receive the funds
     * @param amount funds to be transferred to billing organization
     * @param description details for future reference
     * @return new monthly bill object if successful, error message otherwise
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewMonthlyBill (@RequestParam String cardNumber, @RequestParam Integer PIN, @RequestParam String billingOrganization, @RequestParam Double amount, @RequestParam String description) {
        try{
            //gets card list matching card number. can be null
            Optional<Card> cardList = cardRepository.findById(cardNumber);
            if (!cardList.isPresent()) {
                //returns error message if card not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Card Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            if (!billingOrganizationRepository.findById(billingOrganization).isPresent()) {
                //returns error message if billing organization specified does not exist
                StatusBuilder statusBuilder = new StatusBuilder("error","No Billing Organization Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            Card card = cardList.get();
            //gets account list by account number. can be null
            if (!accountRepository.findById(card.getAccountNumber()).isPresent()) {
                //returns error message if account not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Account Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //authenticates the user's card using PIN
            if (card.getPIN() != PIN) {
                //returns error message if PIN does not match
                StatusBuilder statusBuilder = new StatusBuilder("error","PIN incorrect");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //creates monthly bill if all requirements met, returns monthly bill object
            MonthlyBill monthlyBill = new MonthlyBill(cardNumber, billingOrganization, amount, description);
            monthlyBillRepository.save(monthlyBill);
            return new ResponseEntity<>(monthlyBill, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets monthly bill based on bill ID
     * @param billID identifies the bill
     * @return monthly bill if successful, error message otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getMonthlyBill (@RequestParam Integer billID) {
        try{
            Optional<MonthlyBill> monthlyBillList = monthlyBillRepository.findById(billID);
            if(!monthlyBillList.isPresent()) {
                //returns error message if no monthly bills match bill id
                StatusBuilder statusBuilder = new StatusBuilder("error","No Monthly Bill Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            MonthlyBill monthlyBill = monthlyBillList.get();
            return new ResponseEntity<>(monthlyBill, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets the monthly bill tied to a card
     * @param cardNumber identifies the card
     * @return monthly bill list if successful, error message otherwise
     */
    @RequestMapping(path="/card")
    public ResponseEntity<?> getMonthlyBillByCard (@RequestParam String cardNumber) {
        try{
            //gets the monthly bill list matching card number from db. can be empty
            List<MonthlyBill> monthlyBillList = monthlyBillRepository.findMonthlyBillsByCardNumber(cardNumber);
            if (!monthlyBillList.isEmpty()) {
                //returns monthly bill list if not empty
                return new ResponseEntity<>(monthlyBillList, HttpStatus.OK);
            }
            //returns error message if no monthly bills found or card not found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Monthly Bill Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method gets all monthly bills
     * Does not require any param
     * @return monthly bill list, empty json if db is empty
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllMonthlyBills () {
        try{
            return new ResponseEntity<>(monthlyBillRepository.findAll(), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates the monthly bill based on param specified in request message
     * @param billID identifies a monthly bill(id auto generated by db, id number shared across all tables)
     * @param cardNumber identifies a card the monthly bill is tied to
     * @param billingOrganization organization to send funds to
     * @param amount amount of funds to send
     * @param description details for future reference
     * @return updated monhtly bill object if successful, error message otherwise
     */
    @PostMapping(path="update")
    public ResponseEntity<?> updateMonthlyBill (@RequestParam Integer billID, String cardNumber, String billingOrganization, Double amount, String description) {
        try{
            //gets monthly bill list by bill id. can be null
            Optional<MonthlyBill> monthlyBillList = monthlyBillRepository.findById(billID);
            if(!monthlyBillList.isPresent()) {
                //returns error message if no monthly bills match bill id
                StatusBuilder statusBuilder = new StatusBuilder("error","No Monthly Bill Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            MonthlyBill monthlyBill = monthlyBillList.get();
            //sets specified param if present in request message
            if(cardNumber != null) {
                //gets card matching cardNumber from db
                if (!cardRepository.findById(cardNumber).isPresent()) {
                    //returns error message if no card found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Card Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                monthlyBill.setCardNumber(cardNumber);
            }
            if(billingOrganization != null) {
                //checks if billing organization exists
                if (!billingOrganizationRepository.findById(billingOrganization).isPresent()) {
                    //returns error message if no billing organization not found
                    StatusBuilder statusBuilder = new StatusBuilder("error","No Billing Organization Exists");
                    return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
                }
                monthlyBill.setBillingOrganization(billingOrganization);
            }
            if(amount != null) {
                monthlyBill.setAmount(amount);
            }
            if(description != null) {
                monthlyBill.setDescription(description);
            }

            //updates monthly bill repository, returns updated monthly bill object
            monthlyBillRepository.save(monthlyBill);
            return new ResponseEntity<>(monthlyBill, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes a monthly bill matching bill id
     * @param billID identifies the bill
     * @return success message if successful, error message otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deleteMonthlyBill (@RequestParam Integer billID) {
        try{
            //gets the monthly bill list by bill id. can be null
            Optional<MonthlyBill> monthlyBillList = monthlyBillRepository.findById(billID);
            if(monthlyBillList.isPresent()) {
                monthlyBillRepository.deleteById(billID);
                //returns success message if monthly bill is found and deleted
                StatusBuilder statusBuilder = new StatusBuilder("success","Monthly Bill Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            //returns error message if monthly bill is not found
            StatusBuilder statusBuilder = new StatusBuilder("error","No Monthly Bill Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method deletes all monthly bills in the db
     * Does no check whether db is empty
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllMonthlyBills() {
        try{
            //default crud method. deletes all monthly bills in db
            monthlyBillRepository.deleteAll();
            StatusBuilder statusBuilder = new StatusBuilder("success","Monthly Bill Deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * This method gets billing organization object matching the billing organization name
     * @param organization name of billing organization
     * @return billing organization if successful, error message otherwise
     */
    @PostMapping(path="/billingorganization")
    public ResponseEntity<?> addBillingOrganization (@RequestParam String organization) {
        try{
            //gets billing list matching billing organization name
            Optional<BillingOrganization> billingList = billingOrganizationRepository.findById(organization);
            if(billingList.isPresent()) {
                //returns error message if no billing organization found
                StatusBuilder statusBuilder = new StatusBuilder("error","No Billing Organization Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            //returns billing organization if found
            BillingOrganization billingOrganization = billingList.get();
            billingOrganizationRepository.save(billingOrganization);
            return new ResponseEntity<>(billingOrganization, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method returns a list of billing organization
     * List is generated when application is started
     * @return list of organization
     */
    @RequestMapping(path="/list")
    public ResponseEntity<?> getAllBillingOrganizations() {
        try {
            //default crud method. finds all billing organizations in db
            return new ResponseEntity<>(billingOrganizationRepository.findAll(),HttpStatus.OK );
        }
        catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}