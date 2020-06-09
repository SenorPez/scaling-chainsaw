package com.senorpez.loot.api;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "itemtransactions")
class ItemTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    @JoinColumn(name = "character_id", nullable = false)
    private Character character;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime = new Date();

    @Column
    private String remark;

    public ItemTransaction() {
    }

    public ItemTransaction(Character character, Item item, int quantity, String remark) {
        this.character = character;
        this.item = item;
        this.quantity = quantity;
        this.remark = remark;
    }

    public ItemTransaction(int id, Character character, Item item, int quantity, String remark) {
        this.id = id;
        this.character = character;
        this.item = item;
        this.quantity = quantity;
        this.remark = remark;
    }

    public int getId() {
        return id;
    }

    public Character getCharacter() {
        return character;
    }

    public Item getItem() {
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