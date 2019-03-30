package com.example.greencampus;

import android.app.Application;

import java.util.HashMap;

public class Globals  {

    private String userID;
    private HashMap<String, String> classes;

    public void setUser(String u){
        this.userID="" ;
        this.userID=u;
    }
    public String getUser(){
        return this.userID;
    }

    public void setClass(String className, String classID){
        if(this.classes==null){
            this.classes = new HashMap<>();
        }
        this.classes.put(className, classID);
    }

    public HashMap<String, String> getClasses(){ return this.classes;}

}