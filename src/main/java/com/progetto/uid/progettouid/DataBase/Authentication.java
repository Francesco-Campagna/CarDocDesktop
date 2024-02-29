package com.progetto.uid.progettouid.DataBase;

import com.progetto.uid.progettouid.Model.User;


public class Authentication {
    private static Authentication instance = null;

    private User user = null;

    private Authentication() {}

    public static Authentication getInstance(){
        if (instance == null){
            instance = new Authentication();
        }
        return instance;
    }

    public void login(User user){
        this.user = user;
    }

    public boolean settedUser(){
        return !(this.user == null);
    }

    public void logout(){
        this.user = null;
    }

    public User getUser() {
        if (user != null){
            return user;
        }else{
            return null;
        }
    }
}
