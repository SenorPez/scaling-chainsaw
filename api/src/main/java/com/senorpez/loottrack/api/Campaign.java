package com.senorpez.loottrack.api;

import javax.persistence.*;

@Entity
@Table(name = "campaigns")
class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    Integer getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
