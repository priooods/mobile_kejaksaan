package com.prio.kejaksaan.model;

import com.prio.kejaksaan.service.Calling;

import java.util.List;

public class ModelNotification extends Calling {
    public List<Item> data;
    public class Item{
        public int type;
        public String time, message, detail;
    }
}
