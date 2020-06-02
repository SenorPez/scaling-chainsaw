package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
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
    @JoinColumn
    @ManyToOne
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
                .map(objects -> new InventoryItem(
                        ((Number) objects[0]).intValue(),
                        new Item()
                                .setId(((Number) objects[1]).intValue())
                                .setName((String) objects[2])
//                                .setWeight(Optional.of(BigDecimal.valueOf(((Number) objects[3]).doubleValue())).orElse(null))
                                .setWeight(objects[3] == null ? null : BigDecimal.valueOf(((Number) objects[3]).doubleValue()))
                                .setDetails(objects[4] == null ? null : (String) objects[4])
                                .setCharges(objects[5] == null ? null : ((Number) objects[5]).intValue())
//                                .setWeight(Optional.of(BigDecimal.valueOf(((Number) objects[3]).doubleValue())))
//                                .setDetails((String) objects[4])
//                                .setCharges(((Number) objects[5]).intValue())
                ))
                .collect(Collectors.toList());
        return this;
    }

    static final class InventoryItem extends Item {
        private final Integer quantity;

        InventoryItem(Integer quantity, Item item) {
            this.quantity = quantity;
            this.setId(item.getId());
            this.setName(item.getName());
            this.setWeight(item.getWeight());
            this.setDetails(item.getDetails());
            this.setCharges(item.getCharges());
        }

        Integer getQuantity() {
            return quantity;
        }
    }
}
