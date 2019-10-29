package com.martinwalls.nea.data.models;

public class SearchItem {
    private String name;
    private int id;

    public SearchItem(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}