package com.shabanaj.beloyal.Enums;

public enum Role {
    SUPER_ADMIN(1,"Superadmin"),
    ADMIN(2,"Admin"),
    BUSINESS(3,"Business"),
    WAITER(4,"Waiter"),
    CUSTOMER(5,"Customer");

    private Integer id;
    private String name;
    private String icon;

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
