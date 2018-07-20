package ninja.jira.skeletonkey.app.controller;

import javax.mail.internet.MimeMessage;

import ninja.jira.skeletonkey.app.entity.User;
import ninja.jira.skeletonkey.app.repository.UserRepository;
import ninja.jira.skeletonkey.app.utility.OTP;
import ninja.jira.skeletonkey.app.utility.StatusBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@Controller
@RequestMapping(path="/api/email")
public class EmailController {

    @Autowired
    private JavaMailSender sender;
    @Autowired
    private UserRepository userRepository;

    @PostMapping(path="/send")
    //@ResponseBody
    public ResponseEntity<?> email(@RequestParam String userID) {
        try {
        Optional<User> userList = userRepository.findById(userID);
        if (!userList.isPresent()) {
            StatusBuilder statusBuilder = new StatusBuilder("error","No User Exists");
            return new ResponseEntity<>(statusBuilder, HttpStatus.BAD_REQUEST);
        }
        User user = userList.get();
        sendEmail(user.getEmail());

        StatusBuilder statusBuilder = new StatusBuilder("success","Email Sent");
        return new ResponseEntity<>(statusBuilder ,HttpStatus.OK);

        }catch(Exception e) {
            return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
        }
    }

    private void sendEmail(String toEmail) throws Exception{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setTo(toEmail);
        helper.setText("Enter " + OTP.passCode.toString() + " (OTP) to access your bank services.");
        helper.setSubject("OTP Authentication");

        sender.send(message);
    }
}
