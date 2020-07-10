package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Character;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "characters")
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private CampaignEntity campaignEntity;

    @JoinColumn(name = "character_id")
    @OneToMany
    private Set<InventoryItem> items;

    public CharacterEntity() {
    }

    public CharacterEntity(final Character character, final CampaignEntity campaignEntity) {
        setName(character.getName());
    }

    private void setName(@NotNull final String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        this.name = name;
    }

    private void setCampaignEntity(CampaignEntity campaignEntity) {
        if (campaignEntity == null) throw new IllegalArgumentException("Must provide campaign");
        this.campaignEntity = campaignEntity;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CampaignEntity getCampaignEntity() {
        return campaignEntity;
    }

    public Set<InventoryItem> getItems() {
        return items;
    }
}
