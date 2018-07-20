package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a credit/debit card. Tied to an account. Used to pay monthly bills
 */
@Entity
public class Card{
    @Id
    private String cardNumber;
    private String accountNumber;
    private Integer PIN;
    private String cardType;
    private Double spendingLimit;

    /**
     * Default constructor required by hibernate
     */
    protected Card() {}

    /**
     * Constructor for class Card
     *
     * @param cardNumber uniquely identifies a card
     * @param accountNumber tied to the account with this account number
     * @param PIN authenticates the card user
     * @param cardType specifies the card type (credit/debit)
     * @param spendingLimit the amount that can be spend from the account the card is tied to monthly
     */
    public Card(String cardNumber, String accountNumber, Integer PIN, String cardType, Double spendingLimit) {
        this.cardNumber = cardNumber;
        this.accountNumber = accountNumber;
        this.PIN = PIN;
        this.cardType = cardType;
        this.spendingLimit = spendingLimit;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getPIN() {
        return PIN;
    }

    public void setPIN(Integer PIN) {
        this.PIN = PIN;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public Double getSpendingLimit() {
        return spendingLimit;
    }

    public void setSpendingLimit(Double spendingLimit) {
        this.spendingLimit = spendingLimit;
    }
}