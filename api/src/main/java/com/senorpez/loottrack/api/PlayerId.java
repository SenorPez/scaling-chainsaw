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

    public int getId() {
        return id;
    }

    public PlayerId setId(int id) {
        this.id = id;
        return this;
    }

    public int getCampaign() {
        return campaign;
    }

    public PlayerId setCampaign(int campaign) {
        this.campaign = campaign;
        return this;
    }
}
