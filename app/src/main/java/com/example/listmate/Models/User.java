package com.example.listmate.Models;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String id;
    private String name;
    private String phone;
    private String image;
    private ArrayList <String> reminders = new ArrayList<>();
    private HashMap<String,Integer> shoppingLists = new HashMap<>();

    public User (){

    }

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.phone = email;
    }

    public String getImage() {
        return image;
    }

    public void setReminders(ArrayList<String> reminders) {
        this.reminders = reminders;
    }

    public ArrayList<String> getReminders() {
        return reminders;
    }

    public void addReminder(String reminder) {
        this.reminders.add(reminder);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, Integer> getShoppingLists() {
        return shoppingLists;
    }

    public void setShoppingLists(HashMap<String, Integer> shoppingLists) {
        this.shoppingLists = shoppingLists;
    }

    public User addShoppingList(String listId){
        this.shoppingLists.put(listId,0);
        return this;
    }
}
