package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import ninja.jira.skeletonkey.app.utility.ScheduleConfig;

/**
 * Represents a recurring payment. Requires an account
 */
@Entity
public class RecurringPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer recurringID;
    private String fromAccount;
    private String toAccount;
    private Double amount;
    private String period;
    private String description;

    /**
     * Default constructor required by hibernate
     */
    protected RecurringPayment() {}

    /**
     * Constructor for class RecurringPayment
     *
     * @param fromAccount account funds is being sent from
     * @param toAccount account funds is being sent to
     * @param amount funds to be sent
     * @param period how frequent the transaction occurs (daily/weekly/monthly)
     * @param description details for reference
     */
    public RecurringPayment(String fromAccount, String toAccount, Double amount, String period, String description) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.period = period;
        this.description = description;
    }

    public Integer getRecurringID() {
        return recurringID;
    }

    public void setRecurringID(Integer recurringID) {
        this.recurringID = recurringID;
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

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}