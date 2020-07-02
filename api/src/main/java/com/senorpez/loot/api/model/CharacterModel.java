package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Relation(value = "character", collectionRelation = "character")
public class CharacterModel extends EmbeddedCharacterModel {
    @JsonProperty
    private List<InventoryItemModel> inventory;

    protected CharacterModel setInventory(List<InventoryItemModel> inventory) {
        this.inventory = inventory;
        return this;
    }

//    CharacterModel setInventoryFromDatabase(List<Object[]> inventory) {
//        this.inventory = inventory
//                .stream()
//                .map(InventoryItem::new)
//                .map(InventoryItemModelAssembler::toModel)
//                .collect(Collectors.toList());
//        return this;
//    }
}
