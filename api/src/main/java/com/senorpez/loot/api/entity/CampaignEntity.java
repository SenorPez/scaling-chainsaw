package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Campaign;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "campaigns")
public class CampaignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "campaign_id")
    @OneToMany
    private Set<CharacterEntity> characterEntities;

    public CampaignEntity() {
    }

    public CampaignEntity(final Campaign campaign) {
        setName(campaign.getName());
    }

    private void setName(@NotNull final String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<CharacterEntity> getCharacterEntities() {
        return characterEntities;
    }
}