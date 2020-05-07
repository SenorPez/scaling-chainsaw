package com.senorpez.loottrack.api;

import java.io.Serializable;

public class ItemTransactionId implements Serializable {
    private int id;
    private int player;
    private int campaign;

    public ItemTransactionId() {
    }

    public ItemTransactionId(int id, int player, int campaign) {
        this.id = id;
        this.player = player;
        this.campaign = campaign;
    }
}
