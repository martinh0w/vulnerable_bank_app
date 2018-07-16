package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ninja.jira.skeletonkey.app.entity.*;
import ninja.jira.skeletonkey.app.repository.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class tests the ForeignTransactionController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ForeignTransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ForeignTransactionRepository foreignTransactionRepository;
    @Autowired
    private ForeignCurrencyRepository foreignCurrencyRepository;

    /**
     * Creates two sets of user, account to perform a foreign transaction for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse("2018-01-01");

        userRepository.save(new User("tommy", 123456, "tombom", "tom@example.com", "999"));
        userRepository.save(new User("bob", 123456, "bob", "bob@example.com", "999"));

        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
        accountRepository.save(new Account("321-321-321-321", "bob", "Savings", 5000.0, 1000.0,1000.0));

        foreignTransactionRepository.save(new ForeignTransaction("123-123-123-123", "321-321-321-321", 1000.0, date, "This is a foreign transaction", "USD", 1340.00 ));

    }

    /**
     * Deletes all user, account, foreign transaction after each test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        foreignTransactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * This method tests if users can perform a foreign transaction
     * @throws Exception
     */
    @Test
    public void addNewForeignTransactionPositive() throws Exception {
        mockMvc.perform(post("/api/transaction/foreign/add")
                .param("fromAccount", "321-321-321-321")
                .param("toAccount", "123-123-123-123")
                .param("amount", "1000.0")
                .param("description", "This is a foreign transaction")
                .param("currencyCode", "USD")
                .param("date", "2018-01-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionID\":2,\"fromAccount\":\"321-321-321-321\",\"toAccount\":\"123-123-123-123\",\"amount\":1000.0,\"date\":\"2017-12-31T16:00:00.000+0000\",\"description\":\"This is a foreign transaction\",\"currencyCode\":\"USD\",\"foreignAmount\":1340.0}"));
    }

    /**
     * This method tests if users can use different currency codes to transfer over their account's balance
     * @throws Exception
     */
    @Test
    public void addNewForeignTransactionNegative() throws Exception {
        mockMvc.perform(post("/api/transaction/foreign/add")
                .param("fromAccount", "321-321-321-321")
                .param("toAccount", "123-123-123-123")
                .param("amount", "100000000.0")
                .param("description", "This is a foreign transaction")
                .param("currencyCode", "GBP")
                .param("date", "2018-01-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionID\":2,\"fromAccount\":\"321-321-321-321\",\"toAccount\":\"123-123-123-123\",\"amount\":100000000.0,\"date\":\"2017-12-31T16:00:00.000+0000\",\"description\":\"This is a foreign transaction\",\"currencyCode\":\"GBP\",\"foreignAmount\":179000000}"));
    }

    /**
     * This method tests if foreign currency list can be retrieved
     * @throws Exception
     */
    @Test
    public void getExchangeRatePositive() throws Exception {
        //foreignCurrencyRepository.save(new ForeignCurrency("USD", "US Dollar", 1.34));

        mockMvc.perform(get("/api/transaction/foreign/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"currencyCode\":\"USD\",\"currencyName\":\"US Dollar\",\"exchangeRate\":1.34}," +
                        "{\"currencyCode\":\"EUR\",\"currencyName\":\"Euro\",\"exchangeRate\":1.58}," +
                        "{\"currencyCode\":\"GBP\",\"currencyName\":\"Sterling Pound\",\"exchangeRate\":1.79}," +
                        "{\"currencyCode\":\"AUD\",\"currencyName\":\"Australian Dollar\",\"exchangeRate\":1.02}," +
                        "{\"currencyCode\":\"CAD\",\"currencyName\":\"Canadian Dollar\",\"exchangeRate\":1.03}," +
                        "{\"currencyCode\":\"NZD\",\"currencyName\":\"New Zealand Dollar\",\"exchangeRate\":0.95}," +
                        "{\"currencyCode\":\"DKK\",\"currencyName\":\"Danish Kroner\",\"exchangeRate\":0.21}," +
                        "{\"currencyCode\":\"HKD\",\"currencyName\":\"Hong Kong Dollar\",\"exchangeRate\":0.17}," +
                        "{\"currencyCode\":\"NOK\",\"currencyName\":\"Norwegian Kroner\",\"exchangeRate\":0.16}," +
                        "{\"currencyCode\":\"SEK\",\"currencyName\":\"Swedish Kroner\",\"exchangeRate\":0.15}," +
                        "{\"currencyCode\":\"CHF\",\"currencyName\":\"Swiss Franc\",\"exchangeRate\":1.36}," +
                        "{\"currencyCode\":\"CNY\",\"currencyName\":\"Chinese Renminbi\",\"exchangeRate\":0.21}," +
                        "{\"currencyCode\":\"AED\",\"currencyName\":\"UAE Dirham\",\"exchangeRate\":0.37}]"));
    }
}