package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;
import java.util.List;

public class UserModel extends Calling {
    public static UserModel i;
    public static boolean isExist(){
        return UserModel.i != null;
    }

    public String avatar;
    public String token;
    public String fullname;
    public String name;
    public String type;
    public String log;
    public int id;
    public String password_verified;

    public static int SetnewUsers;

    public UserModel(String tokens){
        this.token = tokens;
    }

    public static int TypeCreateUser;

    public UserModel data;
}
