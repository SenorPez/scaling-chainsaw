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

    private String actorId;

    public FoundryItem() {
    }

    public FoundryItem(String actorId) {
        this.actorId = actorId;
    }

    public Integer getId() {
        return id;
    }

    public String getActorId() {
        return actorId;
    }
}
