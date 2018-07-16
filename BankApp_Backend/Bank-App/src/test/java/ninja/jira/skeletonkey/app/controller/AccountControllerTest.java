package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

/**
 * This class tests the AccountController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * Creates user and account object for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        userRepository.save(new User("tommy", 123456, "tombom", "tom@example.com", "999"));
        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
    }

    /**
     * Deletes user and account object after each test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        userRepository.deleteAll();
        accountRepository.deleteAll();
    }

    /**
     * This method tests whether users can add a new valid account
     * @throws Exception
     */
    @Test
    public void addNewAccountPositive() throws Exception {
        mockMvc.perform(post("/api/account/add")
                .param("accountNumber", "456-456-456-456")
                .param("userID", "tommy")
                .param("accountType", "Savings")
                .param("balance", "5000")
                .param("spendingLimit", "1000")
                .param("withdrawalLimit", "1000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accountNumber\":\"456-456-456-456\",\"userID\":\"tommy\",\"accountType\":\"Savings\",\"balance\":5000.0,\"spendingLimit\":1000.0,\"withdrawalLimit\":1000.0}"));
        accountRepository.deleteById("456-456-456-456");
    }

    /**
     * This method tests whether an account with invalid userID can be added
     * @throws Exception
     */
    @Test
    public void addNewAccountNegative() throws Exception {
        mockMvc.perform(post("/api/account/add")
                .param("accountNumber", "456-456-456-456")
                .param("userID", "tom")
                .param("accountType", "Savings")
                .param("balance", "5000")
                .param("spendingLimit", "1000")
                .param("withdrawalLimit", "1000"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No User Exists\"}"));
    }

    /**
     * This method tests whether a valid account can be retrieved
     * @throws Exception
     */
    @Test
    public void getAccountPositive() throws Exception {
        mockMvc.perform(get("/api/account/get")
                .param("accountNumber", "123-123-123-123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accountNumber\":\"123-123-123-123\",\"userID\":\"tommy\",\"accountType\":\"Savings\",\"balance\":5000.0,\"spendingLimit\":1000.0,\"withdrawalLimit\":1000.0}"));
    }

    /**
     * This method tests whether an invalid account can be retrieved
     * @throws Exception
     */
    @Test
    public void getAccountNegative() throws Exception {
        mockMvc.perform(get("/api/account/get")
                .param("accountNumber", "456-456-456-456"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests whether all accounts can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllAcountsPositive() throws Exception {
        mockMvc.perform(get("/api/account/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"accountNumber\":\"123-123-123-123\",\"userID\":\"tommy\",\"accountType\":\"Savings\",\"balance\":5000.0,\"spendingLimit\":1000.0,\"withdrawalLimit\":1000.0}]"));
    }

    /**
     * This method tests if all accounts belonging to a user can be retrieved
     * @throws Exception
     */
    @Test
    public void getAccountsByUserPositive() throws Exception {
        mockMvc.perform(get("/api/account/user")
                .param("userID", "tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"accountNumber\":\"123-123-123-123\",\"userID\":\"tommy\",\"accountType\":\"Savings\",\"balance\":5000.0,\"spendingLimit\":1000.0,\"withdrawalLimit\":1000.0}]"));
    }

    /**
     * This method tests if all accounts belonging to invalid user can be retrieved
     * @throws Exception
     */
    @Test
    public void getAccountsByUserNegative() throws Exception {
        mockMvc.perform(get("/api/account/user")
                .param("userID", "tom"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests whether a valid account can be updated
     * @throws Exception
     */
    @Test
    public void updateAccountPositive() throws Exception {
        mockMvc.perform(post("/api/account/update")
                .param("accountNumber", "123-123-123-123")
                .param("balance", "1000000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"accountNumber\":\"123-123-123-123\",\"userID\":\"tommy\",\"accountType\":\"Savings\",\"balance\":1000000.0,\"spendingLimit\":1000.0,\"withdrawalLimit\":1000.0}"));
    }

    /**
     * This method tests if an invalid account can be updated
     * @throws Exception
     */
    @Test
    public void updateAccountNegative() throws Exception {
        mockMvc.perform(post("/api/account/update")
                .param("accountNumber", "456-456-456-456"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));

    }

    /**
     * This method tests if a valid account can be deleted
     * @throws Exception
     */
    @Test
    public void deleteAccountPositive() throws Exception {
        mockMvc.perform(post("/api/account/delete")
                .param("accountNumber", "123-123-123-123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Account Deleted\"}"));
    }

    /**
     * This method tests if an invalid account can be deleted
     * @throws Exception
     */
    @Test
    public void deleteAccountNegative() throws Exception {
        mockMvc.perform(post("/api/account/delete")
                .param("accountNumber", "456-456-456-456"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests if all accounts can be deleted from db
     * @throws Exception
     */
    @Test
    public void deleteAllAccountsPositive() throws Exception {
        mockMvc.perform(post("/api/account/deleteall"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Accounts Deleted\"}"));
    }
}