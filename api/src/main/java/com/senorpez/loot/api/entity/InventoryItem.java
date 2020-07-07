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
    private ItemEntity itemEntity;

    @ManyToOne
    private CharacterEntity characterEntity;

    @Transient
    private Integer quantity;

    private String details;

    private Integer charges;

    public InventoryItem() {
    }

    public InventoryItem(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    InventoryItem(Object[] databaseQueryResult) {
        this.id = ((Number) databaseQueryResult[0]).intValue();
        this.itemEntity.name = (String) databaseQueryResult[1];
        this.itemEntity.weight = databaseQueryResult[2] == null ? null : BigDecimal.valueOf(((Number) databaseQueryResult[2]).doubleValue());
        this.quantity = ((Number) databaseQueryResult[3]).intValue();
        this.details = databaseQueryResult[4] == null ? null : (String) databaseQueryResult[4];
        this.charges = databaseQueryResult[5] == null ? null : ((Number) databaseQueryResult[5]).intValue();
    }

    public int getId() {
        return id;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
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
