package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

class InventoryItemModel {
    @JsonProperty
    private int id;
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

    public InventoryItemModel setId(int id) {
        this.id = id;
        return this;
    }

    InventoryItemModel setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    InventoryItemModel setName(String name) {
        this.name = name;
        return this;
    }

    InventoryItemModel setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    InventoryItemModel setDetails(String details) {
        this.details = details;
        return this;
    }

    InventoryItemModel setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
