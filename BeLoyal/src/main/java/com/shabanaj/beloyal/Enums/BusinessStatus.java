package com.shabanaj.beloyal.Enums;

public enum BusinessStatus {
    ACTIVE(1, "Active", "Business can be seen and can operate transactions!",""),
    INACTIVE(1, "Inactive", "Business cannot be seen and cannot operate transactions!",""),
    PENDING_APPROVAL(1, "Approval is pending", "Your application is being verified by the support. This process may take a while!",""),
    REJECTED(1, "Rejected", "Your application was rejected. Please contact support if you think there was something Wrong",""),
    BANNED(1, "Banned", "Your business was banned!","");

    private Integer id;
    private String name;
    private String description;
    private String icon;

    BusinessStatus(Integer id, String name, String description, String icon){
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public Integer getId() {
        return id;
    }

    void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    void setIcon(String icon) {
        this.icon = icon;
    }
}
