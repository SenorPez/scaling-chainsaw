package com.senorpez.loot.api;

import java.math.BigDecimal;

class InventoryItemTemplate {
    public int quantity;
    public String name;
    public BigDecimal weight;
    public String details;
    public Integer charges;

    public InventoryItemTemplate(int quantity, String name, BigDecimal weight, String details, Integer charges) {
        this.quantity = quantity;
        this.name = name;
        this.weight = weight;
        this.details = details;
        this.charges = charges;
    }
}
