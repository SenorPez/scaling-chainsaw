package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @Column(nullable = false)
    @JsonProperty
    private String name;

    @Column(precision = 19, scale=3)
    @JsonProperty
    private BigDecimal weight;

    @Column
    @JsonProperty
    private String details;

    @Column
    @JsonProperty
    private Integer charges;

    Integer getId() {
        return id;
    }

    Item setId(final Integer id) {
        this.id = id;
        return this;
    }

    String getName() {
        return name;
    }

    Item setName(final String name) {
        this.name = name;
        return this;
    }

    BigDecimal getWeight() {
        return weight;
    }

    Item setWeight(final BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    String getDetails() {
        return details;
    }

    Item setDetails(final String details) {
        this.details = details;
        return this;
    }

    Integer getCharges() {
        return charges;
    }

    Item setCharges(final Integer charges) {
        this.charges = charges;
        return this;
    }
}
