package com.senorpez.loottrack.api;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@IdClass(PlayerId.class)
@Table(name = "players")
class Player {
    @Id
    @GeneratedValue(generator = "playerid-gen")
    @GenericGenerator(
            name = "playerid-gen",
            strategy = "com.senorpez.loottrack.api.PlayerIdGenerator"
    )
    private int id;

    @Id
    @ManyToOne
    @JoinColumn
    private Campaign campaign;

    @Column(nullable = false)
    private String name;

    public int getId() {
        return id;
    }

    Player setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
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
