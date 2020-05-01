package com.senorpez.loottrack.api;

import javax.persistence.*;

@Entity
@Table(name = "campaigns")
class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public Campaign setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Campaign setName(String name) {
        this.name = name;
        return this;
    }
}
