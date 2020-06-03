package com.senorpez.loot.api;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@IdClass(CharacterId.class)
@Table(name = "characters")
class Character {
    @Id
    @GeneratedValue(generator = "characterid-gen")
    @GenericGenerator(
            name = "characterid-gen",
            strategy = "com.senorpez.loot.api.CharacterIdGenerator"
    )
    private int id;

    @Id
    @JoinColumn
    @ManyToOne
    private Campaign campaign;

    @Column(nullable = false)
    private String name;

    public Character() {
    }

    public Character(int id, Campaign campaign, String name) {
        this.id = id;
        this.campaign = campaign;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public String getName() {
        return name;
    }

    public Character setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }
}
