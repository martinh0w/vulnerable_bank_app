package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Id;

import java.util.Date;

/**
 * Represents a transaction. Requires two accounts which can belong to the same user
 */
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer transactionID;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    @Temporal(TemporalType.DATE)
    private Date date;
    private String description;

    /**
     * Default constructor required by hibernate
     */
    protected Transaction () {}

    /**
     * Constructor for class Transaction
     *
     * @param fromAccount account funds to be transferred from
     * @param toAccount account funds to be transferred to
     * @param amount amount of funds to be transferred
     * @param date date of transaction
     * @param description details for future reference
     */
    public Transaction(String fromAccount, String toAccount, Double amount, Date date, String description) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Performs a transaction of two valid accounts
     * @param fromAccount account funds to be transferred from
     * @param toAccount account funds to be transferred to
     * @param amount amount to be transferred
     */
    public static void transfer(Account fromAccount, Account toAccount, double amount) {
        double fromBalance = fromAccount.getBalance();
        fromAccount.setBalance(fromBalance - amount);
        double toBalance = toAccount.getBalance();
        toAccount.setBalance(toBalance + amount);
    }

}