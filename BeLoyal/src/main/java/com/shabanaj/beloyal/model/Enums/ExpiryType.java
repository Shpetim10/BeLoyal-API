package com.shabanaj.beloyal.model.Enums;

public enum ExpiryType {
    NO_EXPIRY(1, "No expiry"),
    EXPIRE_AFTER_X_MONTHS(2, "Expire after x months");

    private Integer id;
    private String name;

    ExpiryType(Integer id, String name){
        this.id = id;
        this.name = name;
    }
}
