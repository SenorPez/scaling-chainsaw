package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "character", collectionRelation = "character")
public class EmbeddedCharacterModel extends RepresentationModel<EmbeddedCharacterModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;

    void setId(int id) {
        this.id = id;
    }

    void setName(String name) {
        this.name = name;
    }
}
