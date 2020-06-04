package com.senorpez.loot.api;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 19, scale = 3)
    private BigDecimal weight;

    @Column
    private String details;

    @Column
    private Integer charges;

    public Item() {
    }

    public Item(Integer id, String name, BigDecimal weight, String details, Integer charges) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.details = details;
        this.charges = charges;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    Item setName(String name) {
        this.name = name;
        return this;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    Item setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    public String getDetails() {
        return details;
    }

    Item setDetails(String details) {
        this.details = details;
        return this;
    }

    public Integer getCharges() {
        return charges;
    }

    Item setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
