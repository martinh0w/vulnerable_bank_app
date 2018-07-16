package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.Card;
import ninja.jira.skeletonkey.app.entity.MonthlyBill;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.CardRepository;
import ninja.jira.skeletonkey.app.repository.MonthlyBillRepository;
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
 * This class tests the MonthlyBillController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MonthlyBillControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private MonthlyBillRepository monthlyBillRepository;

    /**
     * Creates a user, account, card, monthly bill for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        userRepository.save(new User("tommy", 123456, "tombom", "tom@example.com", "999"));
        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
        cardRepository.save(new Card("100-000-000", "123-123-123-123", 123, "debit", 5000.0));
        monthlyBillRepository.save(new MonthlyBill("100-000-000", "StarDub Ltd", 50.0, "This is a monthly bill"));
    }

    /**
     * Deletes all user, account, card, monthly bill after every test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        monthlyBillRepository.deleteAll();
        cardRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * This method tests if a valid monthly bill can be added
     * @throws Exception
     */
    @Test
    public void addNewMonthlyBillPositive() throws Exception {
        cardRepository.save(new Card("200-000-000", "123-123-123-123", 123, "debit", 5000.0));

        mockMvc.perform(post("/api/monthlybill/add")
                .param("cardNumber", "200-000-000")
                .param("PIN", "123")
                .param("billingOrganization", "StarDub Ltd")
                .param("amount", "50")
                .param("description", "This is a monthly bill"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"billID\":2,\"cardNumber\":\"200-000-000\",\"billingOrganization\":\"StarDub Ltd\",\"amount\":50.0,\"description\":\"This is a monthly bill\"}"));
    }

    /**
     * This method tests if an invalid card can be added as monthly payment
     * @throws Exception
     */
    @Test
    public void addNewMonthlyBillNegative() throws Exception {
        mockMvc.perform(post("/api/monthlybill/add")
                .param("cardNumber", "300-000-00")
                .param("PIN", "123")
                .param("billingOrganization", "StarDub Ltd")
                .param("amount", "50")
                .param("description", "This is a monthly bill"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Card Exists\"}"));
    }

    /**
     * This method tests if a valid monthly bill can be retrieved by account
     * @throws Exception
     */
    @Test
    public void getMonthlyBillByCardPositive() throws Exception {
        mockMvc.perform(get("/api/monthlybill/get")
                .param("cardNumber", "100-000-000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"billID\":1,\"cardNumber\":\"100-000-000\",\"billingOrganization\":\"StarDub Ltd\",\"amount\":50.0,\"description\":\"This is a monthly bill\"}]"));
    }

    /**
     * This method tests if a monthly bill belonging to an invalid card can be retrieved
     * @throws Exception
     */
    @Test
    public void getMonthlyBillByCardNegative() throws Exception {
        mockMvc.perform(get("/api/monthlybill/get")
                .param("cardNumber", "200-000-000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Monthly Bill Exists\"}"));
    }

    /**
     * This method tests if all monthly bills can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllMonthlyBillPositive() throws Exception {
        mockMvc.perform(get("/api/monthlybill/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"billID\":1,\"cardNumber\":\"100-000-000\",\"billingOrganization\":\"StarDub Ltd\",\"amount\":50.0,\"description\":\"This is a monthly bill\"}]"));
    }

    /**
     * This method tests if a valid monthly bill's monthly payment amount can be updated
     * @throws Exception
     */
    @Test
    public void updateMonthlyBillPositive() throws Exception {
        mockMvc.perform(post("/api/monthlybill/update")
                .param("billID", "1")
                .param("amount", "100"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"billID\":1,\"cardNumber\":\"100-000-000\",\"billingOrganization\":\"StarDub Ltd\",\"amount\":100.0,\"description\":\"This is a monthly bill\"}"));
    }

    /**
     * This method tests if an invalid bill can be updated
     * @throws Exception
     */
    @Test
    public void updateMonthlyBillNegative() throws Exception {
        mockMvc.perform(post("/api/monthlybill/update")
                .param("billID", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Monthly Bill Exists\"}"));
    }

    /**
     * This method tests if a valid monthly bill can be deleted
     * @throws Exception
     */
    @Test
    public void deleteMonthlyBillPositive() throws Exception {
        mockMvc.perform(post("/api/monthlybill/delete")
                .param("billID", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Monthly Bill Deleted\"}"));
    }

    /**
     * This method tests if an invalid monthly bill can be deleted
     * @throws Exception
     */
    @Test
    public void deleteMonthlyBillNegative() throws Exception {
            mockMvc.perform(post("/api/monthlybill/delete")
                    .param("billID", "2"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Monthly Bill Exists\"}"));
    }
}