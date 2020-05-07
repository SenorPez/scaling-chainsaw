package com.senorpez.loottrack.api;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@IdClass(ItemTransactionId.class)
@Table(name = "itemtransactions")
class ItemTransaction {
    @Id
    @GeneratedValue(generator = "itemtransactionid-gen")
    @GenericGenerator(
            name = "itemtransactionid-gen",
            strategy = "com.senorpez.loottrack.api.ItemTransactionIdGenerator"
    )
    private int id;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id")
    @JoinColumn(name = "campaign_id")
    private Player player;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Item item;

    @Column(nullable = false)
    private int quantity;

    public int getId() {
        return id;
    }

    public ItemTransaction setId(int id) {
        this.id = id;
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemTransaction setPlayer(Player player) {
        this.player = player;
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
}