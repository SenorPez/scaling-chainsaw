package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "campaigns")
class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @Column(nullable = false)
    @JsonProperty
    private String name;

    Integer getId() {
        return id;
    }

    Campaign setId(Integer id) {
        this.id = id;
        return this;
    }

    String getName() {
        return name;
    }

    Campaign setName(String name) {
        this.name = name;
        return this;
    }
}
