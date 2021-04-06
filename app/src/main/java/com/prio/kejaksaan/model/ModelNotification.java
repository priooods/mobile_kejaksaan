package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.ArrayList;

public class ModelNotification extends Calling {
    public ArrayList<Item> data;
    public static class Item{
        public int type;
        public String time, message, detail;

        public Item(int type, String time, String message, String detail) {
            this.type = type;
            this.time = time;
            this.message = message;
            this.detail = detail;
        }
        public String toString(){
            return String.valueOf(type)+","+time+","+message+","+detail;
        }
    }
    public Item defItem(){
        return new Item(1,"12:01","Default Notification","This detail is default Notification Model");
    }
}
