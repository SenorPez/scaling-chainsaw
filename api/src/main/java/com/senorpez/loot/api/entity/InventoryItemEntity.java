package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.InventoryItem;

import javax.persistence.*;

@Entity
@Table(name = "inventoryitems")
public class InventoryItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private Integer charges;

    @Column
    private String details;

    @JoinColumn(name = "item_id")
    @ManyToOne
    private ItemEntity itemEntity;

    public InventoryItemEntity() {
    }

    public InventoryItemEntity(final InventoryItem item, final ItemEntity itemEntity) {
        setCharges(item.getCharges());
        setDetails(item.getDetails());
        setItemEntity(itemEntity);
    }

    private void setCharges(final Integer charges) {
        this.charges = charges;
    }

    private void setDetails(final String details) {
        this.details = details;
    }

    private void setItemEntity(final ItemEntity itemEntity) {
        if (itemEntity == null) throw new IllegalArgumentException("Item must not be null");
        this.itemEntity = itemEntity;
    }

    public Integer getId() {
        return id;
    }

    public Integer getCharges() {
        return charges;
    }

    public String getDetails() {
        return details;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }
}
