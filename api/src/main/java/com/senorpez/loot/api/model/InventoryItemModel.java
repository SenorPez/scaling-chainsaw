package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class InventoryItemModel {
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

    public InventoryItemModel setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public InventoryItemModel setName(String name) {
        this.name = name;
        return this;
    }

    public InventoryItemModel setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    public InventoryItemModel setDetails(String details) {
        this.details = details;
        return this;
    }

    public InventoryItemModel setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
