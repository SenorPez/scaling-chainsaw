package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Relation(value = "lootitem", collectionRelation = "lootitem")
class ItemModel extends RepresentationModel<ItemModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private BigDecimal weight;
    @JsonProperty
    private String details;
    @JsonProperty
    private Integer charges;

    ItemModel setId(int id) {
        this.id = id;
        return this;
    }

    ItemModel setName(String name) {
        this.name = name;
        return this;
    }

    ItemModel setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    ItemModel setDetails(String details) {
        this.details = details;
        return this;
    }

    ItemModel setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }
}
