package com.senorpez.loottrack.api;

import java.io.Serializable;

public class ItemTransactionId implements Serializable {
    private int id;
    private Player player;

    public ItemTransactionId() {
    }

    public ItemTransactionId(int id, Player player) {
        this.id = id;
        this.player = player;
    }

    public int getId() {
        return id;
    }

    public ItemTransactionId setId(int id) {
        this.id = id;
        return this;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemTransactionId setPlayer(Player player) {
        this.player = player;
        return this;
    }
}
