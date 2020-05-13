package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Transient
    private List<InventoryItem> inventory = Collections.emptyList();

    public int getId() {
        return id;
    }

    public Player setId(int id) {
        this.id = id;
        return this;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public Player setCampaign(Campaign campaign) {
        this.campaign = campaign;
        return this;
    }

    public String getName() {
        return name;
    }

    public Player setName(String name) {
        this.name = name;
        return this;
    }

    public List<InventoryItem> getInventory() {
        return inventory;
    }

    @JsonIgnore
    public Player setInventory(List<Object[]> inventory) {
        this.inventory = inventory
                .stream()
                .map(objects -> new InventoryItem((String) objects[0], ((BigDecimal) objects[1]).intValue()))
                .collect(Collectors.toList());
        return this;
    }

    static class InventoryItem {
        @JsonProperty
        private final String name;
        @JsonProperty
        private final Integer quantity;

        public InventoryItem(String name, Integer quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }
}
