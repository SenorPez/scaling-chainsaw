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
    private final Date datetime = new Date();

    @Column
    private String remark;

    public ItemTransaction() {
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