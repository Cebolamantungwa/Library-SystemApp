package com.cebo.librarysystem.model;

public class UserModel {
    String initials,last_name,key_number,password,email;

    public UserModel() {
    }

    public UserModel(String initials, String last_name, String key_number, String password) {
        this.initials = initials;
        this.last_name = last_name;
        this.key_number = key_number;
        this.password = password;

    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getKey_number() {
        return key_number;
    }

    public void setKey_number(String key_number) {
        this.key_number = key_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
