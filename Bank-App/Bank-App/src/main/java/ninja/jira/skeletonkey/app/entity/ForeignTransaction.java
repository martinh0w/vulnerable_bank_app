package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.util.Date;

/**
 * Represents a foreign transaction. Is a subclass of transaction. Performs a transaction on accounts with exchange rate applied
 */
@Entity
public class ForeignTransaction extends Transaction {
    private String currencyCode;
    private double foreignAmount;

    /**
     * Default constructor required by hibernate
     */
    protected ForeignTransaction() {}

    /**
     * Constructor for class ForeignTransaction
     *
     * @param fromAccount account funds is being transferred from
     * @param toAccount account funds is being transferred to
     * @param amount the amount to be transferred
     * @param date specifies the date of transaction
     * @param description details for future reference
     * @param currencyCode to determine the exchange rate
     * @param foreignAmount the final amount to be transacted in SGD
     */
    public ForeignTransaction(String fromAccount, String toAccount, Double amount, Date date, String description, String currencyCode, double foreignAmount) {
        super(fromAccount, toAccount, amount, date, description);
        this.currencyCode = currencyCode;
        this.foreignAmount = foreignAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public double getForeignAmount() {
        return foreignAmount;
    }

    public void setForeignAmount(double foreignAmount) {
        this.foreignAmount = foreignAmount;
    }
}