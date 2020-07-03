package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Relation(value = "lootitem", collectionRelation = "lootitem")
public class ItemModel extends RepresentationModel<ItemModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private BigDecimal weight;

    public ItemModel setId(int id) {
        this.id = id;
        return this;
    }

    public ItemModel setName(String name) {
        this.name = name;
        return this;
    }

    public ItemModel setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }
}
