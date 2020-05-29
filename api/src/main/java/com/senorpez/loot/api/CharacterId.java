package com.senorpez.loot.api;

import java.io.Serializable;

public class CharacterId implements Serializable {
    private int id;
    private int campaign;

    public CharacterId() {
    }

    public CharacterId(int character, Campaign campaign) {
        this.id = character;
        this.campaign = campaign.getId();
    }

    public int getId() {
        return id;
    }

    public CharacterId setId(int id) {
        this.id = id;
        return this;
    }

    public int getCampaign() {
        return campaign;
    }

    public CharacterId setCampaign(int campaign) {
        this.campaign = campaign;
        return this;
    }
}
