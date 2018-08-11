package edu.bluejack17_2.tolongku;

import android.util.Log;

public class User {

    private String userContactEmail,userContactNumber,userEmail,userGender,userName,userPhone,userMessage;

    public User(String userContactEmail, String userContactNumber, String userEmail, String userGender, String userName, String userPhone, String userMessage) {
        this.userContactEmail = userContactEmail;
        this.userContactNumber = userContactNumber;
        this.userEmail = userEmail;
        this.userGender = userGender;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userMessage = userMessage;
    }

    public User()
    {}


    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserContactEmail() {
        return userContactEmail;
    }

    public void setUserContactEmail(String userContactEmail) {
        this.userContactEmail = userContactEmail;
    }

    public String getUserContactNumber() {
        return userContactNumber;
    }

    public void setUserContactNumber(String userContactNumber) {
        this.userContactNumber = userContactNumber;
    }

    public String printData()
    {
        return(userContactEmail+" "+userContactNumber+" "+userEmail+" "+userGender+" "+userName+" "+userPhone+" "+userMessage);

    }


    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
