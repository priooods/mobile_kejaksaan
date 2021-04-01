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

    private UserService service;
    public String token;
    public BaseModel(){}
    public BaseModel(String tokens){
        this.token = tokens;
    }

    public UserService getService(){
        if (service == null){
            service = Client.getClient().create(UserService.class);
        }
        return service;
    }
    public static int CheckType;
    public static int StatusPerkara;
}
