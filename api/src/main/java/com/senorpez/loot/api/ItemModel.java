package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Relation(value = "lootitem", collectionRelation = "lootitem")
class ItemModel extends RepresentationModel<ItemModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String name;
    @JsonProperty
    BigDecimal weight;
    @JsonProperty
    String details;
    @JsonProperty
    Integer charges;

    public ItemModel() {
    }

    ItemModel setId(final int id) {
        this.id = id;
        return this;
    }

    ItemModel setName(final String name) {
        this.name = name;
        return this;
    }

    ItemModel setWeight(final BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    ItemModel setDetails(final String details) {
        this.details = details;
        return this;
    }

    ItemModel setCharges(final Integer charges) {
        this.charges = charges;
        return this;
    }
}
