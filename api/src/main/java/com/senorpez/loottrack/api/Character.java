package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@IdClass(CharacterId.class)
@Table(name = "characters")
class Character {
    @Id
    @GeneratedValue(generator = "characterid-gen")
    @GenericGenerator(
            name = "characterid-gen",
            strategy = "com.senorpez.loottrack.api.CharacterIdGenerator"
    )
    private int id;

    @Id
    @ManyToOne
    @JoinColumn
    private Campaign campaign;

    @Column(nullable = false)
    private String name;

    @Transient
    private List<InventoryItem> inventory = Collections.emptyList();

    public int getId() {
        return id;
    }

    public Character setId(int id) {
        this.id = id;
        return this;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Character setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }

    public String getName() {
        return name;
    }

    public Character setName(String name) {
        this.name = name;
        return this;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    @JsonIgnore
    public Character setInventory(List<Object[]> inventory) {
        this.inventory = inventory
                .stream()
                .map(objects -> new InventoryItem((String) objects[0], ((Number) objects[1]).intValue(), (Integer) objects[2]))
                .collect(Collectors.toList());
        return this;
    }

    static class InventoryItem {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final Integer quantity;
        @JsonProperty
        private final Integer charges;

        public InventoryItem(String name, Integer quantity, Integer charges) {
            this.name = name;
            this.quantity = quantity;
            this.charges = charges;
        }

        public String getName() {
            return name;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Integer getCharges() {
            return charges;
        }
    }
}
