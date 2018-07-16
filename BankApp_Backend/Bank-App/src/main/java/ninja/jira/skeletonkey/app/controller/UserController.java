package ninja.jira.skeletonkey.app.controller;

import ninja.jira.skeletonkey.app.utility.*;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.Part;

import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.UserRepository;

/**
 * This is the controller for class User.
 * Uses GET methods when retrieving data from db
 * Uses POST methods otherwise
 */
@CrossOrigin
@Controller
@RequestMapping(path="/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    /**
     * This method adds a new user to the db
     * @param userID identifies the user
     * @param PIN authenticates the user
     * @param fullName particulars of the user
     * @param email particulars of the user
     * @param mobile particulars of the user
     * @param profilePicture accepts all file formats, stored as byte array in db
     * @return user object in json if successful, respective error message otherwise
     */
    @PostMapping(path="/add")
    public ResponseEntity<?> addNewUser (@RequestParam String userID, @RequestParam String PIN, @RequestParam String fullName, @RequestParam String email, @RequestParam String mobile, Part profilePicture) {
        try{
            //finds whether user exists
            if(userRepository.findById(userID).isPresent()){
                StatusBuilder statusBuilder = new StatusBuilder("error","User Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            //profilePicture is not a required param, will be added into db if included in the request message
            if (profilePicture != null) {

                //util method converts multi-part form data to byte array
                byte[] image = Util.convertImage(profilePicture);

                //creates new user and stores in db, returns user object in response
                User n = new User(userID, PIN, fullName, email, mobile, image);
                userRepository.save(n);
                return new ResponseEntity<>(n, HttpStatus.OK);
            }
            //creates new user without profilePicture
            User n = new User(userID, PIN, fullName, email, mobile);

            //stores in db, return user object in response
            userRepository.save(n);
            return new ResponseEntity<>(n, HttpStatus.OK);

        }
        catch(Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }


    /**
     * This method returns all users that have been created from the db. Does not require any param
     * @return all users in json object, empty json if none is created
     */
    @RequestMapping(path="/all")
    public ResponseEntity<?> getAllUsers() {
        try{
            //default crud method. returns all rows in db
            return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method returns particular user object matching a userID
     * @param userID used to identify the user
     * @return user object in json if found, error message otherwise
     */
    @RequestMapping(path="/get")
    public ResponseEntity<?> getUser(@RequestParam String userID) {
        try{
            //gets user list from user db. can be null
            Optional<User> user = userRepository.findById(userID);
            if (user.isPresent()) {
                return new ResponseEntity<>(user, HttpStatus.OK);
            }
            //returns error message if no user found
            StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method finds the profilePicture of the user matching the userID
     * @param userID used to identify the user
     * @return byte array of image if found, error message otherwise
     */
    @RequestMapping(path="/image")
    public ResponseEntity<?> getProfilePictureByUser (@RequestParam String userID) {
        try{
            //gets user list from db. can be null
            Optional<User> userList = userRepository.findById(userID);
            if(!userList.isPresent()) {
                //returns error message if user not found or profilePicture not found
                StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }
            //returns profilePicture if found
            User user = userList.get();
            byte[] image = user.getProfilePicture();
            //encode byte array to base 64 string
            String base64String = Base64.encodeBase64String(image);
            StatusBuilder statusBuilder = new StatusBuilder("success", base64String);
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method updates the user details
     * @param userID identifies the user
     * @param PIN authenticates the user
     * @param fullName particulars of the user
     * @param email particulars of the user
     * @param mobile particulars of the user
     * @param profilePicture accepts all file formats, stored as byte array in db
     * @return updated user object in json if successful, error otherwise
     */
    @PostMapping(path="/update")
    public ResponseEntity<?> updateUser (@RequestParam String userID, String PIN, String fullName, String email, String mobile, Part profilePicture) {
        try{
            //gets user list from user db. can be null
            Optional<User> userList = userRepository.findById(userID);
            if (!userList.isPresent()) {
                //returns error message if no user found with userID
                StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
                return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
            }

            //sets the params inputted by request message
            User user = userList.get();
            if(PIN != null) {
                user.setPIN(PIN);
            }
            if(fullName != null) {
                user.setFullName(fullName);
            }
            if(email != null) {
                user.setEmail(email);
            }
            if(mobile != null) {
                user.setMobile(mobile);
            }
            if(profilePicture != null) {
                //converts the multipart form data to byte array before adding to user object
                byte[] image = Util.convertImage(profilePicture);
                user.setProfilePicture(image);
            }

            //update the details of the user
            userRepository.save(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
        catch(Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method deletes the user object matching with userID
     * @param userID identifies the user
     * @return success message if deleted, error message otherwise
     */
    @PostMapping(path="/delete")
    public ResponseEntity<?> deleteUser(@RequestParam String userID) {
        try {
            //gets the user list from user db. can be null
            Optional<User> userList = userRepository.findById(userID);
            if (userList.isPresent()) {
                //default crud methods. delete all rows corresponding to userID
                userRepository.deleteById(userID);
                //success message that user is deleted
                StatusBuilder statusBuilder = new StatusBuilder("success","User Deleted");
                return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
            }
            //returns error message if user not found in db
            StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * This method deletes all users in db. Does not take in param
     * Does not check whether there is any data in db
     * @return success message
     */
    @PostMapping(path="/deleteall")
    public ResponseEntity<?> deleteAllUsers() {
        try {
            //default crud method. deletes all user in db
            userRepository.deleteAll();
            StatusBuilder statusBuilder = new StatusBuilder("success","Users deleted");
            return new ResponseEntity<>(statusBuilder, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.OK);
        }
    }

    /**
     * This method authenticates the user with the PIN
     * @param userID identifies the user
     * @param PIN authenticates the user
     * @return success if PIN matches, error message otherwise.
     */
    @PostMapping(path="/login")
    public ResponseEntity<?> login(@RequestParam String userID, @RequestParam String PIN) {
        try{
            List<User> userList = userRepository.login(userID, PIN);

            if (userList.isEmpty()) {
                StatusBuilder statusBuilder = new StatusBuilder("error","Invalid user/password");
                return new ResponseEntity<>(statusBuilder,HttpStatus.BAD_REQUEST);
            }
            StatusBuilder statusBuilder = new StatusBuilder("success","Logged In");
            return new ResponseEntity<>(statusBuilder ,HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method authenticates user with OTP, required to access sensitive methods
     * @param passCode to authenticate user
     * @return success if passcode matches, error otherwise
     */
    @PostMapping(path="/authenticate")
    public ResponseEntity<?> authenticate(@RequestParam Integer passCode) {
        try{
            //checks whether passcode matches with OTP class
            if (OTP.authenticate(passCode)) {
                //returns success if matches
                StatusBuilder statusBuilder = new StatusBuilder("success","Authenticated");
                return new ResponseEntity<>(statusBuilder ,HttpStatus.OK);
            }
            //returns error if does not match
            StatusBuilder statusBuilder = new StatusBuilder("error","Invalid OTP");
            return new ResponseEntity<>(statusBuilder,HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }
}