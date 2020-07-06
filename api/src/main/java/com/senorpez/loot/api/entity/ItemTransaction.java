package com.senorpez.loot.api.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "itemtransactions")
public class ItemTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn
    @ManyToOne
    private InventoryItem item;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;

    @Column
    private String remark;

    public ItemTransaction() {
    }

    public ItemTransaction(InventoryItem item, int quantity, Date datetime, String remark) {
        this.item = item;
        this.quantity = quantity;
        this.datetime = datetime;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public InventoryItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getDatetime() {
        return datetime;
    }

    public String getRemark() {
        return remark;
    }
}