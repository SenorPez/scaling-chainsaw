package com.senorpez.loottrack.api;

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
    Date datetime = new Date();

    @Column
    private String remark;

    public int getId() {
        return id;
    }

    public ItemTransaction setId(int id) {
        this.id = id;
        return this;
    }

    public Character getCharacter() {
        return character;
    }

    public ItemTransaction setCharacter(Character character) {
        this.character = character;
        return this;
    }

    public Item getItem() {
        return item;
    }

    public ItemTransaction setItem(Item item) {
        this.item = item;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public ItemTransaction setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public ItemTransaction setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}