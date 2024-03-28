package com.example.remart;


public class Users {
    private String Name;
    private String Email;
    private String Password;
    private String PhoneNumber;

    public Users(String Name, String Email, String Password, String PhoneNumber) {
        this.Name = Name;
        this.Email = Email;
        this.Password = Password;
        this.PhoneNumber = PhoneNumber;
    }


    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

}