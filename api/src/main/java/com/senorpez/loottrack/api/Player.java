package com.senorpez.loottrack.api;

import javax.persistence.*;

@Entity
@IdClass(PlayerId.class)
@Table(name = "players")
class Player {
    @Id
    private int id;

    @Id
    @ManyToOne
    @JoinColumn
    private Campaign campaign;

    @Column(nullable = false)
    private String name;

    int getId() {
        return id;
    }

    Player setId(int id) {
        this.id = id;
        return this;
    }

    String getName() {
        return name;
    }

    Player setName(String name) {
        this.name = name;
        return this;
    }

    int getCampaignId() {
        return campaign.getId();
    }

    Player setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }
}
