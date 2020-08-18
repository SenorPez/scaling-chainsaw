package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.util.HashSet;
import java.util.Set;

@Relation(value = "character", collectionRelation = "character")
public class CharacterModel extends RepresentationModel<CharacterModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    final Set<EmbeddedInventoryItemModel> inventory = new HashSet<>();

    CharacterModel setId(int id) {
        this.id = id;
        return this;
    }

    CharacterModel setName(String name) {
        this.name = name;
        return this;
    }
}
