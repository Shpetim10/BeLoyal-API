package com.shabanaj.beloyal.model.Enums;

public enum Role {
    SUPER_ADMIN(1,"Super Admin"),
    ADMIN(2,"Admin"),
    BUSINESS_ADMIN(3,"Business Admin"),
    STAFF(4,"Staff"),
    CUSTOMER(5,"Customer");

    Integer id;
    String name;
    String icon;

    Role(Integer id, String name, String icon){
        this.id = id;
        this.name = name;
        this.icon = icon;
    }

    Role(Integer id, String name){
        this.id = id;
        this.name = name;
    }
}
