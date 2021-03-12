package com.prio.kejaksaan.model;

public class UserModel {

    public static UserModel i;
    public static boolean isExist(){
        return UserModel.i != null;
    }

    public String avatar;
    public String fullname;
    public String name;
    public String type;
    public String log;
    public String id;
    public String password;
}
