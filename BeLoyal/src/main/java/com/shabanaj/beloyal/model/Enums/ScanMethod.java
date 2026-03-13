package com.shabanaj.beloyal.model.Enums;

public enum ScanMethod {
    MANUAL_CODE(1,"Manual Code"),
    QR_CODE(2, "QR Code"),;

    ScanMethod(int id, String name){
        this.id=id;
        this.name=name;
    }

    private int id;
    private String name;
}
