package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Character;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "characters")
public class CharacterEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "campaign_id")
    @ManyToOne
    private CampaignEntity campaignEntity;

    @JoinColumn(name = "character_id")
    @OneToMany
    private final Set<InventoryItemEntity> items = new HashSet<>();

    public CharacterEntity() {
    }

    public CharacterEntity(final Character character, final CampaignEntity campaignEntity) {
        setName(character.getName());
        setCampaignEntity(campaignEntity);
    }

    public CharacterEntity(final Character character, final CampaignEntity campaignEntity, Set<InventoryItemEntity> inventory) {
        setName(character.getName());
        setCampaignEntity(campaignEntity);
        items.addAll(inventory);
    }

    private void setName(final String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        this.name = name;
    }

    private void setCampaignEntity(final CampaignEntity campaignEntity) {
        if (campaignEntity == null) throw new IllegalArgumentException("Campaign must not be null");
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

    public Set<InventoryItemEntity> getItems() {
        return items;
    }
}
