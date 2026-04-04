package com.shabanaj.beloyal.model.Enums;

public enum CatalogStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private String name;

     CatalogStatus(String name){
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
