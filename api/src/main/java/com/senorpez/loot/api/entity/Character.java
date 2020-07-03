package com.senorpez.loot.api.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "characters")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Campaign campaign;

    @JoinColumn(name = "character_id")
    @OneToMany
    private List<InventoryItem> items;

    public Character() {
    }

    public Character(int id, String name, Campaign campaign, List<InventoryItem> items) {
        this.id = id;
        this.name = name;
        this.campaign = campaign;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public Character setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }
}
