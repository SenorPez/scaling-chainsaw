package com.senorpez.loot.api;

import java.math.BigDecimal;

class InventoryItemTemplate {
    public int quantity;
    public String name;
    public BigDecimal weight;
    public String details;
    public Integer charges;

    InventoryItemTemplate(InventoryItem inventoryItem) {
        this.quantity = inventoryItem.getQuantity();
        this.name = inventoryItem.getName();
        this.weight = inventoryItem.getWeight();
        this.details = inventoryItem.getDetails();
        this.charges = inventoryItem.getCharges();
    }
}
