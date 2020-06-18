package com.senorpez.loot.api;

import java.math.BigDecimal;

public class InventoryItem {
    private final int quantity;
    private final String name;
    private final BigDecimal weight;
    private final String details;
    private final Integer charges;

    InventoryItem(Object[] databaseQueryResult) {
        this.quantity = ((Number) databaseQueryResult[0]).intValue();
        this.name = (String) databaseQueryResult[1];
        this.weight = databaseQueryResult[2] == null ? null : BigDecimal.valueOf(((Number) databaseQueryResult[2]).doubleValue());
        this.details = databaseQueryResult[3] == null ? null : (String) databaseQueryResult[3];
        this.charges = databaseQueryResult[4] == null ? null : ((Number) databaseQueryResult[4]).intValue();
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
