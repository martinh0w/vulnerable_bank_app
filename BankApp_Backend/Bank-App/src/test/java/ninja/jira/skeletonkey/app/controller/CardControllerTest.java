package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.Card;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.CardRepository;
import ninja.jira.skeletonkey.app.repository.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/**
 * This class tests the CardController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CardRepository cardRepository;

    /**
     * Creates user, account, card object for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        userRepository.save(new User("tommy", 123456, "tombom", "tom@example.com", "999"));
        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
        cardRepository.save(new Card("100-000-000", "123-123-123-123", 123, "debit", 5000.0));

    }

    /**
     * Deletes all user, account, card object after each test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        userRepository.deleteAll();
        accountRepository.deleteAll();
        cardRepository.deleteAll();
    }

    /**
     * This method adds an extra card representing random user's card which we do not have access to
     */
    public void addCard() {
        userRepository.save(new User("bobby", 123456, "bobby", "bobby@example.com", "999"));
        accountRepository.save(new Account("456-456-456-456", "bobby", "Savings", 5000.0, 1000.0,1000.0));
        cardRepository.save(new Card("200-000-000", "456-456-456-456", 123, "debit", 5000.0));
    }

    /**
     * This method tests if a new card with existing user and account can be created
     * @throws Exception
     */
    @Test
    public void addNewCardPositive() throws Exception {
        userRepository.save(new User("bob", 123456, "bob", "bob@example.com", "999"));
        accountRepository.save(new Account("321-321-321-321", "bob", "Savings", 5000.0, 1000.0,1000.0));
        mockMvc.perform(post("/api/card/add")
                .param("cardNumber", "300-000-000")
                .param("accountNumber", "321-321-321-321")
                .param("PIN", "123")
                .param("cardType", "debit")
                .param("spendingLimit", "5000.0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"cardNumber\":\"300-000-000\",\"accountNumber\":\"321-321-321-321\",\"cardType\":\"debit\",\"spendingLimit\":5000.0,\"pin\":123}"));
    }

    /**
     * This method tests if a card can be added with invalid account number
     * @throws Exception
     */
    @Test
    public void addNewCardNegative() throws Exception {
        mockMvc.perform(post("/api/card/add")
                .param("cardNumber", "300-000-000")
                .param("accountNumber", "321-321-321-321")
                .param("PIN", "123")
                .param("cardType", "debit")
                .param("spendingLimit", "5000.0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests if a valid card can be retrieved
     * @throws Exception
     */
    @Test
    public void getCardPositive() throws Exception {
        mockMvc.perform(get("/api/card/get")
                .param("cardNumber", "100-000-000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"cardNumber\":\"100-000-000\",\"accountNumber\":\"123-123-123-123\",\"cardType\":\"debit\",\"spendingLimit\":5000.0,\"pin\":123}"));

    }

    /**
     * This method tests if an invalid card can be retrieved
     * @throws Exception
     */
    @Test
    public void getCardNegative() throws Exception {
        mockMvc.perform(get("/api/card/get")
                .param("cardNumber", "300-000-000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Card Exists\"}"));
    }

    /**
     * This method tests if all cards belonging to an account can be retrieved
     * @throws Exception
     */
    @Test
    public void getCardByAccountPositive() throws Exception {
        mockMvc.perform(get("/api/card/account")
                .param("accountNumber", "123-123-123-123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"cardNumber\":\"100-000-000\",\"accountNumber\":\"123-123-123-123\",\"cardType\":\"debit\",\"spendingLimit\":5000.0,\"pin\":123}]"));
    }

    /**
     * This method tests if all cards belonging to other user can be retrieved
     * @throws Exception
     */
    @Test
    public void getCardByAccountNegative() throws Exception {
       addCard();
        mockMvc.perform(get("/api/card/account")
                .param("accountNumber", "456-456-456-456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"cardNumber\":\"200-000-000\",\"accountNumber\":\"456-456-456-456\",\"cardType\":\"debit\",\"spendingLimit\":5000.0,\"pin\":123}]"));
    }

    /**
     * This method tests if all cards in the db can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllCardsPositive() throws Exception {
        mockMvc.perform(get("/api/card/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"cardNumber\":\"100-000-000\",\"accountNumber\":\"123-123-123-123\",\"cardType\":\"debit\",\"spendingLimit\":5000.0,\"pin\":123}]"));
    }

    /**
     * This method tests if a valid card can be updated
     * @throws Exception
     */
    @Test
    public void updateCardPositive() throws Exception {
        mockMvc.perform(post("/api/card/update")
                .param("cardNumber", "100-000-000")
                .param("cardType", "credit"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"cardNumber\":\"100-000-000\",\"accountNumber\":\"123-123-123-123\",\"cardType\":\"credit\",\"spendingLimit\":5000.0,\"pin\":123}"));
    }

    /**
     * This method tests if an invalid card can be updated
     * @throws Exception
     */
    @Test
    public void updateCardNegative() throws Exception {
        mockMvc.perform(post("/api/card/update")
                .param("cardNumber", "300-000-000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Card Exists\"}"));
    }

    /**
     * This method tests if a valid card can be deleted
     * @throws Exception
     */
    @Test
    public void deleteCardPositive() throws Exception {
        mockMvc.perform(post("/api/card/delete")
                .param("cardNumber", "100-000-000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Card Deleted\"}"));
    }

    /**
     * This method tests if cards belonging to other users can be deleted
     * @throws Exception
     */
    @Test
    public void deleteCardNegative() throws Exception {
        addCard();
        mockMvc.perform(post("/api/card/delete")
                .param("cardNumber", "200-000-000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Card Deleted\"}"));
    }

    /**
     * This method tests if all cards can be deleted from the db
     * @throws Exception
     */
    @Test
    public void deleteAllCards() throws Exception {
        mockMvc.perform(post("/api/card/deleteall"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Cards Deleted\"}"));
    }
}