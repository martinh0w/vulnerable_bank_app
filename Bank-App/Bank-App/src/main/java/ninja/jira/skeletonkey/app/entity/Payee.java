package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a payee. Requires at least 2 different users with an account in order to create
 */
@Entity
public class Payee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer payeeID;
    private String accountNumber;
    private String userID;
    private String bank;
    private String fullName;
    private String initials;

    /**
     * Default constructor for hibernate
     */
    protected Payee() {}

    /**
     * Constructor for class Payee
     *
     * @param accountNumber payee's account number
     * @param userID belongs to user adding the payee
     * @param bank payee's bank
     * @param fullName name of the payee
     * @param initials preference to identify payee
     */
    public Payee(String accountNumber, String userID, String bank, String fullName, String initials) {
        this.accountNumber = accountNumber;
        this.userID = userID;
        this.bank = bank;
        this.fullName = fullName;
        this.initials = initials;
    }


    public Integer getPayeeID() {
        return payeeID;
    }

    public void setPayeeID(Integer payeeID) {
        this.payeeID = payeeID;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }
}