package com.senorpez.loot.api.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "campaigns")
public class CampaignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "campaign_id")
    @OneToMany
    private List<CharacterEntity> characterEntities;

    public CampaignEntity() {
    }

    public CampaignEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CharacterEntity> getCharacterEntities() {
        return characterEntities;
    }

    public CampaignEntity setCharacterEntities(List<CharacterEntity> characterEntities) {
        this.characterEntities = characterEntities;
        return this;
    }
}