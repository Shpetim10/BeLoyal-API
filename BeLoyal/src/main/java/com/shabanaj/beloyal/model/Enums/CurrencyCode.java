package com.shabanaj.beloyal.model.Enums;

public enum CurrencyCode {
    LEK("Leke", "ALL"),
    DOLLAR("Dollar", "$"),
    EURO("Euro", "€");

    private String name;
    private String code;

     CurrencyCode(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
