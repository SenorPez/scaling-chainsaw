package com.senorpez.loottrack.api;

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

    @Column(precision = 19, scale=3)
    private BigDecimal weight;

    @Column
    private String details;

    @Column
    private Integer charges;

    Integer getId() {
        return id;
    }

    Item setId(Integer id) {
        this.id = id;
        return this;
    }

    String getName() {
        return name;
    }

    Item setName(String name) {
        this.name = name;
        return this;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public Item setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public Item setDetails(String details) {
        this.details = details;
        return this;
    }

    public Integer getCharges() {
        return charges;
    }

    public Item setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
