package ninja.jira.skeletonkey.app.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.UserRepository;

import org.apache.tomcat.util.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

/**
 * This class tests the UserController class
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    /**
     * Creates a user object for each test
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        File profilePicture = new File("../storage/Test.jpg");
        byte[] image =  new byte[(int) profilePicture.length()];
        userRepository.save(new User("tommy", 123456, "tombom", "tom@example.com", "999", image));
    }

    /**
     * Deletes user object after each test
     * @throws Exception
     */
    @After
    public void reset() throws Exception {
        userRepository.deleteAll();
    }

    /**
     * This method tests whether a user can be added successfully
     * @throws Exception
     */
    @Test
    public void addNewUserPositive() throws Exception {
        File profilePicture = new File("../storage/Test.jpg");
        byte[] image =  new byte[(int) profilePicture.length()];
        mockMvc.perform(post("/api/user/add")
                .param("userID","tommy2")
                .param("PIN", "123456")
                .param("fullName", "tombom")
                .param("email", "tom@example.com")
                .param("mobile", "999"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"userID\":\"tommy2\",\"fullName\":\"tombom\",\"email\":\"tom@example.com\",\"mobile\":\"999\",\"pin\":123456}"));

        userRepository.deleteById("tommy2");
    }

    /**
     * This method tests whether users with invalid PIN can be added
     * @throws Exception
     */
    @Test
    public void addNewUserNegative() throws Exception {
        mockMvc.perform(post("/api/user/add")
                .param("userID","tom")
                .param("PIN", "asd123")
                .param("fullName", "tombom")
                .param("email", "tom@example.com")
                .param("mobile", "999"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    /**
     * This method tests if users can be retrieved
     * @throws Exception
     */
    @Test
    public void getUserPositive() throws Exception {
        mockMvc.perform(get("/api/user/get")
                .param("userID","tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"userID\":\"tommy\",\"fullName\":\"tombom\",\"email\":\"tom@example.com\",\"mobile\":\"999\",\"pin\":123456}"));
    }

    /**
     * This method tests if invalid userID retrieves a user
     * @throws Exception
     */
    @Test
    public void getUserNegative() throws Exception {
        mockMvc.perform(get("/api/user/get")
                .param("userID", "tom"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No User Exists\"}"));
    }

    /**
     * This method tests whether a list of users can be retrieved
     * @throws Exception
     */
    @Test
    public void getAllUsersPositive() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"userID\":\"tommy\",\"fullName\":\"tombom\",\"email\":\"tom@example.com\",\"mobile\":\"999\",\"pin\":123456}]"));
    }

    /**
     * This method tests whether a user can login with correct userID and PIN
     * @throws Exception
     */
    @Test
    public void loginUserPositive() throws Exception {
        mockMvc.perform(post("/api/user/login")
                .param("userID", "tommy")
                .param("PIN", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Logged in\"}"));
    }

    /**
     * This method tests whether a user can login with invalid PIN
     * @throws Exception
     */
    @Test
    public void loginUserNegative() throws Exception {
        mockMvc.perform(post("/api/user/login")
                .param("userID", "tommy")
                .param("PIN", "654321"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"Invalid user/password\"}"));
    }

    /**
     * This method tests a valid OTP authentication
     * @throws Exception
     */
    @Test
    public void authenticateUserPositive() throws Exception {
        mockMvc.perform(post("/api/user/authenticate")
                .param("passCode", "123456"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"Authenticated\"}"));
    }

    /**
     * This method tests an invalid OTP authentication
     * @throws Exception
     */
    @Test
    public void authenticateUserNegative() throws Exception {
        mockMvc.perform(post("/api/user/authenticate")
                .param("passCode", "123"))
                .andDo(print())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"Invalid OTP\"}"));
    }

    /**
     * This method tests if correct image can be retrieved fro db
     * @throws Exception
     */
    @Test
    public void getUserProfilePicturePositive() throws Exception {
        File profilePicture = new File("../storage/Test.jpg");
        byte[] image =  new byte[(int) profilePicture.length()];
        String base64String = Base64.encodeBase64String(image);
        mockMvc.perform(get("/api/user/image")
                .param("userID", "tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(base64String)));
    }


    /**
     * This method tests if different file formats can be uploaded
     * @throws Exception
     */
    @Test
    public void getUserProfilePictureNegative() throws Exception {
        File profilePicture = new File ("../storage/Test.docx");
        byte[] image = new byte[(int) profilePicture.length()];
        String base64String = Base64.encodeBase64String(image);
        mockMvc.perform(get("/api/user/image")
                .param("userID", "tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.containsString(base64String)));
    }

    /**
     * This method tests if user object can be updated
     * @throws Exception
     */
    @Test
    public void updateUserPositive() throws Exception {
        mockMvc.perform(post("/api/user/update")
                .param("userID", "tommy")
                .param("PIN", "654321"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"userID\":\"tommy\",\"fullName\":\"tombom\",\"email\":\"tom@example.com\",\"mobile\":\"999\",\"pin\":654321}"));
    }

    /**
     * This method tests if invalid user can be updated
     * @throws Exception
     */
    @Test
    public void updateUserNegative() throws Exception {
        mockMvc.perform(post("/api/user/update")
                .param("userID", "tommy2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No User Exists\"}"));
    }

    /**
     * This method tests if valid user can be deleted
     * @throws Exception
     */
    @Test
    public void deleteUserPositive() throws Exception {
        mockMvc.perform(post("/api/user/delete")
                .param("userID", "tommy"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"success\",\"message\":\"User Deleted\"}"));
    }

    /**
     * This method tests if invalid users can be deleted
     * @throws Exception
     */
    @Test
    public void deleteUserNegative() throws Exception {
        mockMvc.perform(post("/api/user/delete")
                .param("userID", "tom"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"status\":\"error\",\"message\":\"No User Exists\"}"));
    }

    /**
     * This method tests if all users can be deleted from db
     * @throws Exception
     */
    @Test
    public void deleteAllUsersPositive() throws Exception{
        mockMvc.perform(post("/api/user/deleteall"))
                .andDo(print()).andExpect(status().isOk());
    }
}
