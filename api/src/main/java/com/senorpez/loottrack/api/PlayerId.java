package com.senorpez.loottrack.api;

import java.io.Serializable;

public class PlayerId implements Serializable {
    private int id;
    private int campaign;

    public PlayerId() {
    }

    public PlayerId(int player, Campaign campaign) {
        this.id = player;
        this.campaign = campaign.getId();
    }
}
