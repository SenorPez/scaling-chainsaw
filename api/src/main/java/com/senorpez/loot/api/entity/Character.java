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
    private CampaignEntity campaignEntity;

    @JoinColumn(name = "character_id")
    @OneToMany
    private List<InventoryItem> items;

    public Character() {
    }

    public Character(int id, CampaignEntity campaignEntity, String name, List<InventoryItem> items) {
        this.id = id;
        this.name = name;
        this.campaignEntity = campaignEntity;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CampaignEntity getCampaignEntity() {
        return campaignEntity;
    }

    public List<InventoryItem> getItems() {
        return items;
    }

    public Character setCampaignEntity(CampaignEntity campaignEntity) {
        this.campaignEntity = campaignEntity;
        return this;
    }
}
