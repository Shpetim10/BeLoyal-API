package com.shabanaj.beloyal.Enums;

public enum BusinessType {
    RESTAURANT(1,"Restaurant",""),
    BAR(2,"Bar",""),
    CAFE(3,"Cafe",""),
    PUB(4,"Pub",""),
    FAST_FOOD(5,"Fast Food",""),
    OTHER(6,"Other",""),;

    Integer id;
    String name;
    String description;
    String icon;

    BusinessType(Integer id, String name, String description, String icon){
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    BusinessType(Integer id, String name, String description){
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
