package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Relation(value = "character", collectionRelation = "character")
class CharacterModel extends RepresentationModel<CharacterModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;
    @JsonProperty
    List<Character.InventoryItem> inventory;

    CharacterModel() {
    }

    CharacterModel setId(int id) {
        this.id = id;
        return this;
    }

    CharacterModel setName(String name) {
        this.name = name;
        return this;
    }

    CharacterModel setInventory(List<Character.InventoryItem> inventory) {
        this.inventory = inventory;
        return this;
    }
}
