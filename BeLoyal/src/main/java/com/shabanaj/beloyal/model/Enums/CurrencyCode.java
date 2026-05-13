package com.shabanaj.beloyal.model.Enums;

public enum CurrencyCode {
    ALL("Albanian Lek", "ALL"),
    EUR("Euro", "€"),
    USD("US Dollar", "$");

    private final String displayName;
    private final String symbol;

    CurrencyCode(String displayName, String symbol) {
        this.displayName = displayName;
        this.symbol = symbol;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSymbol() {
        return symbol;
    }
}
