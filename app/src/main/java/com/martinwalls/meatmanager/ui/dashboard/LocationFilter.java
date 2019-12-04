package com.martinwalls.meatmanager.ui.dashboard;

public class LocationFilter {

    private int id;
    private String name;
    private boolean filterAll;

    public LocationFilter(int id, String name) {
        this.id = id;
        this.name = name;
        filterAll = false;
    }

    public LocationFilter(boolean filterAll) {
        this.filterAll = filterAll;
        id = -1;
        name = "";
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean filterAll() {
        return filterAll;
    }
}
