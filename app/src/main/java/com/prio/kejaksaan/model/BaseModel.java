package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.service.Client;
import com.prio.kejaksaan.service.UserService;

import java.util.List;

public class BaseModel extends Calling {

    public static BaseModel i;
    public static boolean isExist(){
        return BaseModel.i != null;
    }

    public String token;
    private UserService service;
    public String name;
    public String password;

    public UserModel data;

    public BaseModel(String name, String password){
        this.name = name;
        this.password = password;
    }

    public BaseModel(String token){
        this.token = token;
    }

    public void setToken(String token){
        this.token = token;
    }

    public UserService getService(){
        if (service == null){
            service = Client.getClient().create(UserService.class);
        }
        return service;
    }
}
