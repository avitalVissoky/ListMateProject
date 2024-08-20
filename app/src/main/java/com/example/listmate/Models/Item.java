package com.example.listmate.Models;

public class Item {

    private String listId;
    private String id;
    private String title;
    private String note;
    private boolean isChecked;

    public Item() {
    }

    public Item(String listId, String title, String note, boolean isChecked){
        this.listId = listId;
        this.title = title;
        this.isChecked = isChecked;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTitle() {
        return title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
