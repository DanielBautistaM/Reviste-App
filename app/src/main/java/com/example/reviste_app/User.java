package com.example.reviste_app;

public class User {
    public String username;
    public String email;
    public String fullname;
    public String birthdate;

    public User() {
    }

    public User(String username, String email, String fullname, String birthdate) {
        this.username = username;
        this.email = email;
        this.fullname = fullname;
        this.birthdate = birthdate;
    }
}

