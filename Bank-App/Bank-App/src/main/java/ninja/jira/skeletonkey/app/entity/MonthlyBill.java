package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Id;

/**
 * Represents a monthly bill. Used to pay organizations monthly with credit/debit card
 */
@Entity
public class MonthlyBill {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer billID;
    private String cardNumber;
    private String billingOrganization;
    private Double amount;
    private String description;

    /**
     * Default constructor required by hibernate
     */
    protected MonthlyBill() {}

    /**
     * Constructor for class MonthlyBill
     *
     * @param cardNumber card used to pay for the transaction monthly
     * @param billingOrganization the organization to receive payment
     * @param amount the amount of funds to be transacted
     * @param description details for future reference
     */
    public MonthlyBill(String cardNumber, String billingOrganization, Double amount, String description) {
        this.cardNumber = cardNumber;
        this.billingOrganization = billingOrganization;
        this.amount = amount;
        this.description = description;
    }

    public Integer getBillID() {
        return billID;
    }

    public void setBillID(Integer billID) {
        this.billID = billID;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getBillingOrganization() {
        return billingOrganization;
    }

    public void setBillingOrganization(String billingOrganization) {
        this.billingOrganization = billingOrganization;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}