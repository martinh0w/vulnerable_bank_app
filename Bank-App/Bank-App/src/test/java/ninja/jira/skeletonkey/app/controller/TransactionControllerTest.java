package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.Account;
import ninja.jira.skeletonkey.app.entity.Transaction;
import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.AccountRepository;
import ninja.jira.skeletonkey.app.repository.TransactionRepository;
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

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * This class tests the TransactionController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Creates two sets of user, account to perform a transaction for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse("2018-01-01");
        userRepository.save(new User("tommy", "123456", "tombom", "tom@example.com", "999"));
        userRepository.save(new User("bob", "123456", "bob", "bob@example.com", "999"));


        accountRepository.save(new Account("123-123-123-123", "tommy", "Savings", 5000.0, 1000.0,1000.0));
        accountRepository.save(new Account("321-321-321-321", "bob", "Savings", 5000.0, 1000.0,1000.0));

        transactionRepository.save(new Transaction("123-123-123-123", "321-321-321-321", 1000.0, date, "This is a transaction"));
    }

    /**
     * Deletes all user, account, transaction after each test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();
    }

    /**
     * This method adds an extra transaction representing random user's transaction which we do not have access to
     * @throws Exception
     */
    public void addTransaction() throws Exception{
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse("2018-01-01");
        transactionRepository.save(new Transaction("321-321-321-321", "123-123-123-123", 1000.0, date, "This is a transaction"));
    }

    /**
     * This method tests if new transaction with valid accounts can be added
     * @throws Exception
     */
    @Test
    public void addNewTransactionPositive() throws Exception {
        mockMvc.perform(post("/api/transaction/add")
                .param("fromAccount", "123-123-123-123")
                .param("toAccount", "321-321-321-321")
                .param("amount", "100.0")
                .param("description", "This is a transaction")
                .param("date", "2018-01-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionID\":2,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":100.0,\"date\":\"2017-12-31T16:00:00.000+0000\",\"description\":\"This is a transaction\"}"));

    }

    /**
     * This method tests if users can transfer over their account's balance
     * @throws Exception
     */
    @Test
    public void addNewTransactionNegative() throws Exception {
        mockMvc.perform(post("/api/transaction/add")
                .param("fromAccount", "123-123-123-123")
                .param("toAccount", "321-321-321-321")
                .param("amount", "100000000.0")
                .param("description", "This is a transaction")
                .param("date", "2018-01-01"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionID\":2,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":100000000.0,\"date\":\"2017-12-31T16:00:00.000+0000\",\"description\":\"This is a transaction\"}"));
    }

    /**
     * This method tests if a valid transaction can be retrieved using transaction id
     * @throws Exception
     */
    @Test
    public void getTransactionPositive() throws Exception {
        mockMvc.perform(get("/api/transaction/get")
                .param("transactionID", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}"));
    }

    /**
     * This method tests if an invalid transaction can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionNegative() throws Exception {
        mockMvc.perform(get("/api/transaction/get")
                .param("transactionID", "2"))
                .andDo(print())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Transaction Exists\"}"));
    }

    /**
     * This method tests if transactions belonging to a valid user can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByUserPositive() throws Exception {
        mockMvc.perform(get("/api/transaction/user")
                .param("userID", "tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}]"));
    }

    /**
     * This method tests if transactions belonging to an invalid user can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByUserNegative() throws Exception {
        addTransaction();
        mockMvc.perform(get("/api/transaction/user")
                .param("userID", "alice"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Transaction Exists\"}"));
    }


    /**
     * This method tests if transactions belonging to a valid account can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByAccountPositive() throws Exception {
        mockMvc.perform(get("/api/transaction/account")
                .param("fromAccount", "123-123-123-123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}]"));
    }

    /**
     * This method tests if transactions belonging to another user can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByAccountNegative() throws Exception {
        addTransaction();
        mockMvc.perform(get("/api/transaction/account")
                .param("fromAccount", "321-321-321-321"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionID\":2,\"fromAccount\":\"321-321-321-321\",\"toAccount\":\"123-123-123-123\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}]"));
    }

    /**
     * This method tests if transactions specifying valid spending and receiving account can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByPayeePositive() throws Exception {
        mockMvc.perform(get("/api/transaction/payee")
                .param("fromAccount", "123-123-123-123")
                .param("toAccount", "321-321-321-321"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}]"));
    }

    /**
     * This method tests if transactions specifying invalid receiving account can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByPayeeNegative() throws Exception {
        mockMvc.perform(get("/api/transaction/payee")
                .param("fromAccount", "123-123-123-123")
                .param("toAccount", "456-456-456-456"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Transaction Exists\"}"));
    }

    /**
     * This method tests if transactions belonging to a valid account within a date range can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByDatePositive() throws Exception {
        mockMvc.perform(get("/api/transaction/date")
                .param("fromAccount", "123-123-123-123")
                .param("fromDate", "2018-01-01")
                .param("toDate", "2018-01-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}]"));
    }

    /**
     * This method tests if transactions belonging to an invalid account within a date range can be retrieved
     * @throws Exception
     */
    @Test
    public void getTransactionByDateNegative() throws Exception {
        mockMvc.perform(get("/api/transaction/date")
                .param("fromAccount", "456-456-456-456")
                .param("fromDate", "2018-06-01")
                .param("toDate", "2018-06-30"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Account Exists\"}"));
    }

    /**
     * This method tests if all transactions can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllTransactionsPositive() throws Exception {
        mockMvc.perform(get("/api/transaction/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a transaction\"}]"));
    }

    /**
     * This method tests if a valid transaction can be updated with new description
     * @throws Exception
     */
    @Test
    public void updateTransactionPositive() throws Exception {
        mockMvc.perform(post("/api/transaction/update")
                .param("transactionID", "1")
                .param("description", "This is a updated transaction"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"transactionID\":1,\"fromAccount\":\"123-123-123-123\",\"toAccount\":\"321-321-321-321\",\"amount\":1000.0,\"date\":\"2018-01-01\",\"description\":\"This is a updated transaction\"}"));
    }

    /**
     * This method tests if an invalid transaction can be updated
     * @throws Exception
     */
    @Test
    public void updateTransactionNegative() throws Exception {
        mockMvc.perform(post("/api/transaction/update")
                .param("transactionID", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Transaction Exists\"}"));
    }

    /**
     * This method tests if a valid transaction can be deleted
     * @throws Exception
     */
    @Test
    public void deleteTransactionPositive() throws Exception {
        mockMvc.perform(post("/api/transaction/delete")
                .param("transactionID", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Transaction Deleted\"}"));
    }

    /**
     * This method tests if an invalid transaction can be deleted
     * @throws Exception
     */
    @Test
    public void deleteTransactionNegative() throws Exception {
        mockMvc.perform(post("/api/transaction/delete")
                .param("transactionID", "2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No Transaction Exists\"}"));
    }

    /**
     * This method tests if all transactions can be deleted
     * @throws Exception
     */
    @Test
    public void deleteAllTransactionsPositive() throws Exception {
        mockMvc.perform(post("/api/transaction/deleteall"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Transactions Deleted\"}"));
    }
}