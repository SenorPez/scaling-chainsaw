package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.ItemTransaction;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "itemtransactions")
public class ItemTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    private String remark;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private final Date datetime = new Date();

    @JoinColumn(name = "inv_item_id")
    @ManyToOne
    private InventoryItemEntity inventoryItemEntity;

    public ItemTransactionEntity() {
    }

    public ItemTransactionEntity(final ItemTransaction itemTransaction, final InventoryItemEntity inventoryItemEntity) {
        setQuantity(itemTransaction.getQuantity());
        setRemark(itemTransaction.getRemark());
        setInventoryItemEntity(inventoryItemEntity);
    }

    private void setQuantity(final Integer quantity) {
        if (quantity == null) throw new IllegalArgumentException("Quantity must not be null");
        this.quantity = quantity;
    }

    private void setRemark(final String remark) {
        this.remark = remark;
    }

    private void setInventoryItemEntity(final InventoryItemEntity inventoryItemEntity) {
        if (inventoryItemEntity == null) throw new IllegalArgumentException("Inventory item must not be null");
        this.inventoryItemEntity = inventoryItemEntity;
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getRemark() {
        return remark;
    }

    public Date getDatetime() {
        return datetime;
    }

    public InventoryItemEntity getInventoryItemEntity() {
        return inventoryItemEntity;
    }
}