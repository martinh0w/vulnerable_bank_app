package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents an account. Required in order to perform transactions
 */
@Entity
public class Account {
    @Id
    private String accountNumber;
    private String userID;
    private String accountType;
    private Double balance;
    private Double spendingLimit;
    private Double withdrawalLimit;

    /**
     * Default constructor required by hibernate
     */
    protected Account() {}

    /**
     * Constructor for Account class
     *
     * @param accountNumber uniquely identifies an account
     * @param userID identifies which user the account belongs to
     * @param accountType type of account (checking/savings)
     * @param balance amount of money in account
     * @param spendingLimit specifies the amount that can be spent per month
     * @param withdrawalLimit specifies the amount that can be withdrawn per month
     */
    public Account(String accountNumber, String userID, String accountType, Double balance, Double spendingLimit, Double withdrawalLimit) {
        this.accountNumber = accountNumber;
        this.userID = userID;
        this.accountType = accountType;
        this.balance = balance;
        this.spendingLimit = spendingLimit;
        this.withdrawalLimit = withdrawalLimit;
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }

    public Double getWithdrawalLimit() {
        return withdrawalLimit;
    }

    public void setWithdrawalLimit(Double withdrawalLimit) {
        this.withdrawalLimit = withdrawalLimit;
    }


    public boolean canSpend (Double amount) {
        if (amount <= spendingLimit) {
            return true;
        }
        return false;
    }
}