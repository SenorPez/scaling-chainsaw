package com.senorpez.loot.api;

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
            strategy = "com.senorpez.loot.api.CharacterIdGenerator"
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
                .map(objects -> new InventoryItem(((Number) objects[0]).intValue(), (String) objects[1], ((Number) objects[2]).intValue(), (Integer) objects[3]))
                .collect(Collectors.toList());
        return this;
    }

    static class InventoryItem {
        @JsonProperty
        private final Integer id;
        @JsonProperty
        private final String name;
        @JsonProperty
        private final Integer quantity;
        @JsonProperty
        private final Integer charges;

        public InventoryItem(Integer id, String name, Integer quantity, Integer charges) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.charges = charges;
        }

        public Integer getId() {
            return id;
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
