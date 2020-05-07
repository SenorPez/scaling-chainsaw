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
    @JoinColumn
    private Campaign campaign;

    @Id
    @ManyToOne
    @JoinColumn
    private Player player;

    @ManyToOne
    @JoinColumn
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

    public Campaign getCampaign() {
        return campaign;
    }

    public ItemTransaction setCampaign(Campaign campaign) {
        this.campaign = campaign;
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

    int getCampaignId() {
        return campaign.getId();
    }

    int getPlayerId() {
        return player.getId();
    }

    String getItemName() {
        return item.getName();
    }
}