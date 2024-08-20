package com.example.listmate.Models;

import java.util.HashMap;

public class ShoppingList {

    private String id;
    private String listName;
    private String lastUpdated;
    private HashMap<String,Integer> itemsIds;
    private HashMap<String,String> listPartners;



    public ShoppingList() {}

    public ShoppingList(String id, String listName, String lastUpdated, HashMap<String, Integer> itemsIds) {
        this.id = id;
        this.listName = listName;
        this.lastUpdated = lastUpdated;
        this.itemsIds = itemsIds;
    }

    public HashMap<String, String> getListPartners() {
        return listPartners;
    }

    public void setListPartners(HashMap<String, String> listPartners) {
        this.listPartners = listPartners;
    }
    public void addListPartner(String partnersId, String partnersPhone){
        listPartners.put(partnersId,partnersPhone);
    }
    public void  removeListPartner(String partnersId){
        listPartners.remove(partnersId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public HashMap<String, Integer> getItemsIds() {
        return itemsIds;
    }

    public void setItemsIds(HashMap<String, Integer> itemsIds) {
        this.itemsIds = itemsIds;
    }

}
