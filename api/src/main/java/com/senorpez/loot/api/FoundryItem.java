package com.senorpez.loot.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;

@Entity
@Table(name = "foundryitems")
@JsonDeserialize(using = FoundryItemDeserializer.class)
class FoundryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String actorId;

    @Column(nullable = false)
    private String baseItemId;

    @Column
    private String name;

    @Column
    private Integer quantity;

    @Column
    private Double weight;

    @Column
    private Double price;

    public FoundryItem() {
    }

    public FoundryItem(String actorId, String baseItemId, String name, Integer quantity, Double weight, Double price) {
        this.actorId = actorId;
        this.baseItemId = baseItemId;
        this.name = name;
        this.quantity = quantity;
        this.weight = weight;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public String getActorId() {
        return actorId;
    }

    public String getBaseItemId() {
        return baseItemId;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getPrice() {
        return price;
    }
}
