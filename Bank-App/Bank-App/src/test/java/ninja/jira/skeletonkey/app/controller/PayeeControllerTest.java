package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.Payee;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.PayeeRepository;
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
 * This class tests the PayeeController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PayeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PayeeRepository payeeRepository;

    /**
     * Creates user, account, payee objects for every test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        userRepository.save(new User("tommy", "123456", "tombom", "tom@example.com", "999"));
        userRepository.save(new User("bob", "123456", "bob", "bob@example.com", "999"));

        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
        accountRepository.save(new Account("321-321-321-321", "bob", "Savings", 5000.0, 1000.0,1000.0));

        payeeRepository.save(new Payee("321-321-321-321", "tommy", "ABCBANK", "bob", "B"));
    }

    /**
     * Deletes all user, account, payee object after every test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        userRepository.deleteAll();
        accountRepository.deleteAll();
        payeeRepository.deleteAll();
    }

    /**
     * This method adds an extra payee representing random user's payee which we do not have access to
     */
    public void addPayee() {
        payeeRepository.save(new Payee("123-123-123-123", "bob", "ABCBANK", "tommy", "T"));
    }

    /**
     *This method tests if a new payee with valid user and account can be added
     * @throws Exception
     */
    @Test
    public void addNewPayeePositive() throws Exception {
        userRepository.save(new User("alice", "123456", "alice", "alice@example.com", "999"));
        accountRepository.save(new Account("456-456-456-456", "alice", "Savings", 5000.0, 1000.0,1000.0));
        mockMvc.perform(post("/api/payee/add")
                .param("accountNumber", "456-456-456-456")
                .param("userID", "alice")
                .param("bank", "ABCBANK")
                .param("fullName", "alice")
                .param("initials", "A"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"payeeID\":2,\"accountNumber\":\"456-456-456-456\",\"userID\":\"alice\",\"bank\":\"ABCBANK\",\"fullName\":\"alice\",\"initials\":\"A\"}"));

        payeeRepository.deleteById(2);
        accountRepository.deleteById("456-456-456-456");
        userRepository.deleteById("alice");
    }

    /**
     * This method tests if payee with invalid account can be added
     * @throws Exception
     */
    @Test
    public void addNewPayeeNegative() throws Exception {
        mockMvc.perform(post("/api/payee/add")
                .param("accountNumber", "789-789-789-789")
                .param("userID", "tommy")
                .param("bank", "ABCBANK")
                .param("fullName", "harry")
                .param("initials", "H"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests if a valid payee can be retrieved
     * @throws Exception
     */
    @Test
    public void getPayeePositive() throws Exception {
        mockMvc.perform(get("/api/payee/get")
                .param("userID", "tommy")
                .param("accountNumber", "321-321-321-321"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"payeeID\":1,\"accountNumber\":\"321-321-321-321\",\"userID\":\"tommy\",\"bank\":\"ABCBANK\",\"fullName\":\"bob\",\"initials\":\"B\"}]"));
    }

    /**
     * This method tests if payee belonging to other user can be retrieved
     * @throws Exception
     */
    @Test
    public void getPayeeNegative() throws Exception {
        addPayee();
        mockMvc.perform(get("/api/payee/get")
                .param("userID", "tom")
                .param("accountNumber", "321-321-321-321"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Payee Exists\"}"));
    }

    /**
     * This method tests if all payees belonging to a user can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllPayeesByUserPositive() throws Exception {
        mockMvc.perform(get("/api/payee/user")
                .param("userID", "tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"payeeID\":1,\"accountNumber\":\"321-321-321-321\",\"userID\":\"tommy\",\"bank\":\"ABCBANK\",\"fullName\":\"bob\",\"initials\":\"B\"}]"));
    }

    /**
     * This method tests if all payees belonging to an invalid user can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllPayeesByUserNegative() throws Exception {
        mockMvc.perform(get("/api/payee/user")
                .param("userID", "alice"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Payee Exists\"}"));
    }

    /**
     * This method tests if all payees can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllPayeesPositive() throws Exception {
        mockMvc.perform(get("/api/payee/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"payeeID\":1,\"accountNumber\":\"321-321-321-321\",\"userID\":\"tommy\",\"bank\":\"ABCBANK\",\"fullName\":\"bob\",\"initials\":\"B\"}]"));
    }

    /**
     * This method tests if a valid payee can be updated
     * @throws Exception
     */
    @Test
    public void updatePayeePositive() throws Exception {
        mockMvc.perform(post("/api/payee/update")
                .param("payeeID", "1")
                .param("bank", "CBABANK"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"payeeID\":1,\"accountNumber\":\"321-321-321-321\",\"userID\":\"tommy\",\"bank\":\"CBABANK\",\"fullName\":\"bob\",\"initials\":\"B\"}"));
    }

    /**
     * This method tests if an invalid payee can be updated
     * @throws Exception
     */
    @Test
    public void updatePayeeNegative() throws Exception {
        mockMvc.perform(post("/api/payee/update")
                .param("payeeID", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Payee Exists\"}"));
    }

    /**
     * This method tests if a valid payee can be deleted
     * @throws Exception
     */
    @Test
    public void deletePayeePositive() throws Exception {
        mockMvc.perform(post("/api/payee/delete")
                .param("payeeID", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Payee Deleted\"}"));
    }

    /**
     * This method tests if a payee belonging to other user can be deleted
     * @throws Exception
     */
    @Test
    public void deletePayeeNegative() throws Exception {
        addPayee();
        mockMvc.perform(post("/api/payee/delete")
                .param("payeeID", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Payee Deleted\"}"));
    }

    /**
     * This method tests if all payees can be deleted from db
     * @throws Exception
     */
    @Test
    public void deleteAllPayeesPositive() throws Exception {
        mockMvc.perform(post("/api/payee/deleteall"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Payees Deleted\"}"));
    }
}