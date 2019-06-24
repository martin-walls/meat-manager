package com.martinwalls.nea;

public class Currency {
    private String name;
    private boolean isFavourite = false;

    public Currency(String name) {
        this.name = name;
    }

    public Currency(String name, boolean isFavourite) {
        this.name = name;
        this.isFavourite = isFavourite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }
}
