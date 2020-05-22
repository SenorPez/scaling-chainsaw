package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "lootitem", collectionRelation = "lootitem")
class EmbeddedItemModel extends RepresentationModel<EmbeddedItemModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;

    EmbeddedItemModel() {
    }

    EmbeddedItemModel setId(final int id) {
        this.id = id;
        return this;
    }

    EmbeddedItemModel setName(final String name) {
        this.name = name;
        return this;
    }
}
