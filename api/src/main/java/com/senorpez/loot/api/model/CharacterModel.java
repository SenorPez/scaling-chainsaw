package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Relation(value = "character", collectionRelation = "character")
public class CharacterModel extends RepresentationModel<CharacterModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;
    @JsonProperty
    private List<InventoryItemModel> inventory;

    public CharacterModel setId(int id) {
        this.id = id;
        return this;
    }

    public CharacterModel setName(String name) {
        this.name = name;
        return this;
    }

    public CharacterModel setInventory(List<InventoryItemModel> inventory) {
        this.inventory = inventory;
        return this;
    }
}
