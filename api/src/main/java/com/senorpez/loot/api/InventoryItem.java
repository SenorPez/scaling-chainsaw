package com.senorpez.loot.api;

import java.math.BigDecimal;

public class InventoryItem {
    private final int id;
    private final Integer quantity;
    private final String name;
    private final BigDecimal weight;
    private final String details;
    private final Integer charges;

    InventoryItem(Object[] databaseQueryResult) {
        this.id = ((Number) databaseQueryResult[0]).intValue();
        this.quantity = ((Number) databaseQueryResult[1]).intValue();
        this.name = (String) databaseQueryResult[2];
        this.weight = databaseQueryResult[3] == null ? null : BigDecimal.valueOf(((Number) databaseQueryResult[3]).doubleValue());
        this.details = databaseQueryResult[4] == null ? null : (String) databaseQueryResult[4];
        this.charges = databaseQueryResult[5] == null ? null : ((Number) databaseQueryResult[5]).intValue();
    }

    public int getId() {
        return id;
    }

    Integer getQuantity() {
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
