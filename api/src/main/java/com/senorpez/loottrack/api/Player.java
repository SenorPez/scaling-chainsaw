package com.senorpez.loottrack.api;

import javax.persistence.*;

@Entity
@Table(name = "players")
class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Campaign campaign;

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
