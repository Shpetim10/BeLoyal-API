package com.shabanaj.beloyal.model.Enums;

public enum CatalogItemType {
    PRODUCT("Product"),
    SERVICE("Service");

    CatalogItemType(String name){
        this.name=name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
