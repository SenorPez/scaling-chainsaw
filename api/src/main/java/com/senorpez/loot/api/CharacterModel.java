package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.stream.Collectors;

@Relation(value = "character", collectionRelation = "character")
class CharacterModel extends RepresentationModel<CharacterModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private List<InventoryItemModel> inventory;

    CharacterModel setId(int id) {
        this.id = id;
        return this;
    }

    CharacterModel setName(String name) {
        this.name = name;
        return this;
    }

    CharacterModel setInventory(List<InventoryItemModel> inventory) {
        this.inventory = inventory;
        return this;
    }

    CharacterModel setInventoryFromDatabase(List<Object[]> inventory) {
        this.inventory = inventory
                .stream()
                .map(InventoryItem::new)
                .map(InventoryItemModelAssembler::toModel)
                .collect(Collectors.toList());
        return this;
    }
}
