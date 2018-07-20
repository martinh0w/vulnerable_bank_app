package ninja.jira.skeletonkey.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import ninja.jira.skeletonkey.app.entity.ForeignCurrency;
import ninja.jira.skeletonkey.app.entity.BillingOrganization;
import ninja.jira.skeletonkey.app.repository.ForeignCurrencyRepository;
import ninja.jira.skeletonkey.app.repository.BillingOrganizationRepository;


/**
 * Default main application for project.
 */
@SpringBootApplication
public class Application {

    /**
     * Runs the whole project.
     * @param args unused
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Inserts values into db upon starting of application via commandlinerunner
     *
     * @param foreignCurrencyRepository this param is auto configured by beans on startup
     * @param billingOrganizationRepository this param is auto configured by beans on startup
     * @return does not return anything
     */
    @Bean
    CommandLineRunner init(ForeignCurrencyRepository foreignCurrencyRepository, BillingOrganizationRepository billingOrganizationRepository) {
        //Inserts the exchange rate table into the foreign currency db
        foreignCurrencyRepository.save(new ForeignCurrency("USD", "US Dollar", 1.34));
        foreignCurrencyRepository.save(new ForeignCurrency("EUR", "Euro", 1.58));
        foreignCurrencyRepository.save(new ForeignCurrency("GBP", "Sterling Pound", 1.79));
        foreignCurrencyRepository.save(new ForeignCurrency("AUD", "Australian Dollar", 1.02));
        foreignCurrencyRepository.save(new ForeignCurrency("CAD", "Canadian Dollar", 1.03));
        foreignCurrencyRepository.save(new ForeignCurrency("NZD", "New Zealand Dollar", 0.95));
        foreignCurrencyRepository.save(new ForeignCurrency("DKK", "Danish Kroner", 0.21));
        foreignCurrencyRepository.save(new ForeignCurrency("HKD", "Hong Kong Dollar", 0.17));
        foreignCurrencyRepository.save(new ForeignCurrency("NOK", "Norwegian Kroner", 0.16));
        foreignCurrencyRepository.save(new ForeignCurrency("SEK", "Swedish Kroner", 0.15));
        foreignCurrencyRepository.save(new ForeignCurrency("CHF", "Swiss Franc", 1.36));
        foreignCurrencyRepository.save(new ForeignCurrency("CNY", "Chinese Renminbi", 0.21));
        foreignCurrencyRepository.save(new ForeignCurrency("AED", "UAE Dirham", 0.37));

        //inserts list of billing organization into billing organization db
        billingOrganizationRepository.save(new BillingOrganization("Singapore Express Credit Card"));
        billingOrganizationRepository.save(new BillingOrganization("Great Western Insurance Company"));
        billingOrganizationRepository.save(new BillingOrganization("StarDub Ltd"));
        billingOrganizationRepository.save(new BillingOrganization("Ministry of Education"));
        billingOrganizationRepository.save(new BillingOrganization("NTUD Membership"));
        billingOrganizationRepository.save(new BillingOrganization("Singapore Country Club"));

        return null;
    }
}