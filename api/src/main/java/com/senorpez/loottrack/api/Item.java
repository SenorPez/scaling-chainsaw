package com.senorpez.loottrack.api;

import javax.persistence.*;

@Entity
@Table(name = "items")
class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

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
}
