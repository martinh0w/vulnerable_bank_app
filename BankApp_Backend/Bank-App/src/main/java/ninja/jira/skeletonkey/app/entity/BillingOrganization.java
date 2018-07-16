package ninja.jira.skeletonkey.app.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Represents a billing organization. Used when paying monthly bills
 */
@Entity
public class BillingOrganization {
    @Id
    private String organization;

    /**
     * Default constructor required by hibernate
     */
    protected BillingOrganization() {}

    /**
     * Constructor for class Billing Organization
     *
     * @param organization name of the organization
     */
    public BillingOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }
}