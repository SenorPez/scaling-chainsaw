package com.senorpez.loottrack.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "lootitem", collectionRelation = "lootitem")
class ItemModel extends RepresentationModel<ItemModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;
    @JsonProperty
    Integer charges;

    public ItemModel() {
    }

    ItemModel setId(int id) {
        this.id = id;
        return this;
    }

    ItemModel setName(String name) {
        this.name = name;
        return this;
    }

    public ItemModel setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
