package com.senorpez.loot.api;

import java.math.BigDecimal;

public class InventoryItem {
    private int quantity;
    private String name;
    private BigDecimal weight;
    private String details;
    private Integer charges;

    InventoryItem() {
    }

    InventoryItem(Object[] databaseQueryResult) {
        this.quantity = ((Number) databaseQueryResult[0]).intValue();
        this.name = (String) databaseQueryResult[1];
        this.weight = databaseQueryResult[2] == null ? null : BigDecimal.valueOf(((Number) databaseQueryResult[2]).doubleValue());
        this.details = databaseQueryResult[3] == null ? null : (String) databaseQueryResult[3];
        this.charges = databaseQueryResult[4] == null ? null : ((Number) databaseQueryResult[4]).intValue();
    }

    InventoryItem(int quantity, String name, BigDecimal weight, String details, Integer charges) {
        this.quantity = quantity;
        this.name = name;
        this.weight = weight;
        this.details = details;
        this.charges = charges;
    }

    int getQuantity() {
        return quantity;
    }

    String getName() {
        return name;
    }

    BigDecimal getWeight() {
        return weight;
    }

    String getDetails() {
        return details;
    }

    Integer getCharges() {
        return charges;
    }
}
