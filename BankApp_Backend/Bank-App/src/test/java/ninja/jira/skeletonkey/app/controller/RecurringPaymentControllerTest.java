package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.RecurringPayment;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.RecurringPaymentRepository;
import ninja.jira.skeletonkey.app.repository.UserRepository;

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

/**
 * This class tests the RecurringPaymentController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RecurringPaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RecurringPaymentRepository recurringPaymentRepository;

    /**
     * Creates two sets of user, account to perform one recurring payment for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        userRepository.save(new User("tommy", 123456, "tombom", "tom@example.com", "999"));
        userRepository.save(new User("bob", 123456, "bob", "bob@example.com", "999"));

        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
        accountRepository.save(new Account("321-321-321-321", "bob", "Savings", 5000.0, 1000.0,1000.0));

        recurringPaymentRepository.save(new RecurringPayment("123-123-123-123", "321-321-321-321", 50.0, "daily", "This is a recurring payment"));
    }

    /**
     * Deletes all user, account, recurring payment after each test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        recurringPaymentRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * This method tests if a valid recurring payment can be added
     * @throws Exception
     */
    @Test
    public void addNewRecurringPaymentPositive() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/add")
                .param("fromAccount", "321-321-321-321")
                .param("toAccount", "123-123-123-123")
                .param("amount", "50.0")
                .param("period", "daily")
                .param("description", "This is a recurring payment"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"recurringID\":2,\"fromAccount\":\"321-321-321-321\",\"toAccount\":\"123-123-123-123\",\"amount\":50.0,\"period\":\"daily\",\"description\":\"This is a recurring payment\"}"));
    }

    /**
     * This method tests if an invalid account can be added as recurring payment
     * @throws Exception
     */
    @Test
    public void addNewRecurringPaymentNegative() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/add")
                .param("fromAccount", "123-123-123-123")
                .param("toAccount", "456-456-456-456")
                .param("amount", "50.0")
                .param("period", "daily")
                .param("description", "This is a recurring payment"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests if a valid recurring payment can be retrieved
     * @throws Exception
     */
    @Test
    public void getRecurringPaymentByAccountPositive() throws Exception {
        mockMvc.perform(get("/api/recurringpayment/get")
                .param("fromAccount", "123-123-123-123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"recurringID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":50.0,\"period\":\"daily\",\"description\":\"This is a recurring payment\"}]"));
    }

    /**
     * This method tests if an invalid recurring payment can be retrieved
     * @throws Exception
     */
    @Test
    public void getRecurringPaymentByAccountNegative() throws Exception {
        mockMvc.perform(get("/api/recurringpayment/get")
                .param("fromAccount", "456-456-456-456"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Recurring Payment Exists\"}"));
    }

    /**
     * This method tests if a valid recurring payment can be retrieved
     * @throws Exception
     */
    @Test
    public void getRecurringPaymentByPeriodPositive() throws Exception {
        mockMvc.perform(get("/api/recurringpayment/period")
                .param("fromAccount", "123-123-123-123")
                .param("period", "daily"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"recurringID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":50.0,\"period\":\"daily\",\"description\":\"This is a recurring payment\"}]"));
    }

    /**
     * This method tests if recurring payment with invalid paying periods can be retrieved
     * @throws Exception
     */
    @Test
    public void getRecurringPaymentByPeriodNegative() throws Exception {
        mockMvc.perform(get("/api/recurringpayment/period")
                .param("fromAccount", "123-123-123-123")
                .param("period", "test"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Recurring Payment Exists\"}"));
    }

    /**
     * This method tests if all recurring payments can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllRecurringPaymentPositive() throws Exception {
        mockMvc.perform(get("/api/recurringpayment/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"recurringID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":50.0,\"period\":\"daily\",\"description\":\"This is a recurring payment\"}]"));
    }

    /**
     * This method tests if a valid recurring payment's payment amount can be updated
     * @throws Exception
     */
    @Test
    public void updateRecurringPaymentPositive() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/update")
                .param("recurringID", "1")
                .param("amount", "100.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"recurringID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":100.0,\"period\":\"daily\",\"description\":\"This is a recurring payment\"}"));
    }

    /**
     * This method tests if an invalid recurring payment can be updated
     * @throws Exception
     */
    @Test
    public void updateRecurringPaymentNegative() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/update")
                .param("recurringID", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Recurring Payment Exists\"}"));
    }

    /**
     * This method tests if a valid recurring payment can be deleted
     * @throws Exception
     */
    @Test
    public void deleteRecurringPaymentPositive() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/delete")
                .param("recurringID", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Recurring Payment Deleted\"}"));
    }

    /**
     * This method tests if an invalid recurring payment can be deleted
     * @throws Exception
     */
    @Test
    public void deleteRecurringPaymentNegative() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/delete")
                .param("recurringID", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Recurring Payment Exists\"}"));
    }

    /**
     * This method tests if all recurring payment can be deleted
     * @throws Exception
     */
    @Test
    public void deleteAllRecurringPaymentPositive() throws Exception {
        mockMvc.perform(post("/api/recurringpayment/deleteall"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Recurring Payments Deleted\"}"));
    }
}