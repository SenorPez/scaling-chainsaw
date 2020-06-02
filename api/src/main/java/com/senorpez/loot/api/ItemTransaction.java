package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonProperty
    private Item item;

    @Column(nullable = false)
    @JsonProperty
    private int quantity;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date datetime = new Date();

    @Column
    private String remark;

    int getId() {
        return id;
    }

    ItemTransaction setId(int id) {
        this.id = id;
        return this;
    }

    Character getCharacter() {
        return character;
    }

    ItemTransaction setCharacter(Character character) {
        this.character = character;
        return this;
    }

    Item getItem() {
        return item;
    }

    ItemTransaction setItem(Item item) {
        this.item = item;
        return this;
    }

    int getQuantity() {
        return quantity;
    }

    ItemTransaction setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    String getRemark() {
        return remark;
    }

    ItemTransaction setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}