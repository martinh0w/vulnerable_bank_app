package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a foreign currency. Used to generate list of exchange currencies and rates
 */
@Entity
public class ForeignCurrency {
    @Id
    private String currencyCode;
    private String currencyName;
    private Double exchangeRate;

    /**
     * Default constructor required by hibernate
     */
    protected ForeignCurrency() {}

    /**
     * Constructor for class ForeignCurrency
     *
     * @param currencyCode represents the country in 3 letter alphabet
     * @param currencyName name of the currency
     * @param exchangeRate current exchange rate for SGD
     */
    public ForeignCurrency(String currencyCode, String currencyName, Double exchangeRate) {
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.exchangeRate = exchangeRate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}