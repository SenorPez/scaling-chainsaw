package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

class InventoryItem {
    @JsonProperty
    private int quantity;
    @JsonProperty
    private String name;
    @JsonProperty
    private BigDecimal weight;
    @JsonProperty
    private String details;
    @JsonProperty
    private Integer charges;

    public InventoryItem setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public InventoryItem setName(String name) {
        this.name = name;
        return this;
    }

    public InventoryItem setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    public InventoryItem setDetails(String details) {
        this.details = details;
        return this;
    }

    public InventoryItem setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
