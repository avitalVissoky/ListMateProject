package com.example.listmate.Models;

public class ListIntro {

    private String listId;
    private String listName;
    private int precentCompleted;
    private String lastUpdated;

    public ListIntro (String listId, String listName, int precentCompleted, String lastUpdated){
        this.listId = listId;
        this.listName = listName;
        this.precentCompleted = precentCompleted;
        this.lastUpdated = lastUpdated;
    }

    public String getListId() {
        return listId;
    }

    public String getListName() {
        return listName;
    }

    public int getPrecentCompleted() {
        return precentCompleted;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}
