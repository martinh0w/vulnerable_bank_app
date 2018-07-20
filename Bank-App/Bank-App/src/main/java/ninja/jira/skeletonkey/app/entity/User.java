package ninja.jira.skeletonkey.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * Represents a user. Required when creating account and adding payee
 */
@Entity
public class User {
    @Id
    private String userID;
    private String PIN;
    private String fullName;
    private String email;
    private String mobile;
    @Lob
    @Column(columnDefinition="longblob")
    private byte[] profilePicture;

    /**
     * Default constructor required by hibernate
     */
    protected User() {}


    /**
     * Constructor for class User, without profile picture
     *
     * @param userID to uniquely identify a particular user
     * @param PIN  to authenticate the user when logging in
     * @param fullName particulars of the user
     * @param email particulars of the user
     * @param mobile particulars of the user
     */
    public User(String userID, String PIN, String fullName, String email, String mobile) {
        this.userID = userID;
        this.PIN = PIN;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
    }

    /**
     * Constructor for class User
     *
     * @param userID to uniquely identify a particular user
     * @param PIN  to authenticate the user when logging in
     * @param fullName particulars of the user
     * @param email particulars of the user
     * @param mobile particulars of the user
     * @param profilePicture particulars of the user
     */
    public User(String userID, String PIN, String fullName, String email, String mobile, byte[] profilePicture) {
        this.userID = userID;
        this.PIN = PIN;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.profilePicture = profilePicture;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPIN() {
        return PIN;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonIgnore
    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }
}