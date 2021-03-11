package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;
import com.prio.kejaksaan.service.Client;
import com.prio.kejaksaan.service.UserService;

public class UsersModel extends Calling {

    public static UsersModel i;
    public static boolean isExist(){
        return UsersModel.i != null;
    }

    public String token;
    private UserService service;
    public String name;
    public String password;
    public String avatar;
    public String fullname;
    public String type;
    public String log;
    public String id;

    public UsersModel(String username, String password) {
        this.name = username;
        this.password = password;
        i = this;
    }

    public UsersModel(String token){
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
