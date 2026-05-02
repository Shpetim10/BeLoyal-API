package com.shabanaj.beloyal.model.Enums;

public enum PointsType {
    EARN_BILL(1, "Earn"),
    REDEEM_DISCOUNT(2, "Redeem"),
    REDEEM_OFFER(3, "Redeem Offer"),
    EXPIRE(4, "Expire"),
    ADJUSTMENT_PLUS(5, "Adjustment Plus"),
    ADJUSTMENT_MINUS(6, "Adjustment Minus"),
    REVERSAL(7, "Reversal"),
    COUPON_REDEMPTION(8, "Coupon Redemption"),;

    private int id;
    private String name;

    PointsType(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
