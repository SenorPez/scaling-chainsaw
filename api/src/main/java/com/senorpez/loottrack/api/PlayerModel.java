package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Relation(value = "player", collectionRelation = "player")
class PlayerModel extends RepresentationModel<PlayerModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;
    @JsonProperty
    List<Player.InventoryItem> inventory;

    PlayerModel() {
    }

    PlayerModel setId(int id) {
        this.id = id;
        return this;
    }

    PlayerModel setName(String name) {
        this.name = name;
        return this;
    }

    PlayerModel setInventory(List<Player.InventoryItem> inventory) {
        this.inventory = inventory;
        return this;
    }
}
