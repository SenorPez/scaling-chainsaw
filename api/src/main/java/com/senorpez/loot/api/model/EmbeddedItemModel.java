package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "lootitem", collectionRelation = "lootitem")
public class EmbeddedItemModel extends RepresentationModel<EmbeddedItemModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;

    EmbeddedItemModel() {
    }

    public EmbeddedItemModel setId(final int id) {
        this.id = id;
        return this;
    }

    public EmbeddedItemModel setName(final String name) {
        this.name = name;
        return this;
    }
}
