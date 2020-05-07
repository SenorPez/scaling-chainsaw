package com.senorpez.loottrack.api;

import javax.persistence.*;

@Entity
@Table(name = "campaigns")
class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
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
