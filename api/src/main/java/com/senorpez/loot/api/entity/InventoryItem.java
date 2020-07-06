package com.senorpez.loot.api.entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "inventoryitems")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Item item;

    @ManyToOne
    private Character character;

    @Transient
    private Integer quantity;

    private String details;

    private Integer charges;

    public InventoryItem() {
    }

    public InventoryItem(Item item) {
        this.item = item;
    }

    InventoryItem(Object[] databaseQueryResult) {
        this.id = ((Number) databaseQueryResult[0]).intValue();
        this.item.name = (String) databaseQueryResult[1];
        this.item.weight = databaseQueryResult[2] == null ? null : BigDecimal.valueOf(((Number) databaseQueryResult[2]).doubleValue());
        this.quantity = ((Number) databaseQueryResult[3]).intValue();
        this.details = databaseQueryResult[4] == null ? null : (String) databaseQueryResult[4];
        this.charges = databaseQueryResult[5] == null ? null : ((Number) databaseQueryResult[5]).intValue();
    }

    public int getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getDetails() {
        return details;
    }

    public Integer getCharges() {
        return charges;
    }
}
