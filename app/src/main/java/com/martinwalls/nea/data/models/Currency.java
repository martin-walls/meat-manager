package com.martinwalls.nea.data.models;

public class Currency {
    private String code;
    private String name;
    private boolean isFavourite = false;

    public Currency() {}

    public Currency(String name) {
        this.name = name;
    }

    public Currency(String name, boolean isFavourite) {
        this.name = name;
        this.isFavourite = isFavourite;
    }

    public Currency(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public Currency(String code, String name, boolean isFavourite) {
        this.name = name;
        this.code = code;
        this.isFavourite = isFavourite;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public boolean toggleFavourite() {
        isFavourite = !isFavourite;
        return isFavourite;
    }
}
