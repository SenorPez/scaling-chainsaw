package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

@Relation(value = "inventoryitems", collectionRelation = "inventoryitems")
public class EmbeddedInventoryItemModel extends RepresentationModel<EmbeddedInventoryItemModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private BigDecimal weight;
    @JsonProperty
    private Integer charges;
    @JsonProperty
    private String details;
    @JsonProperty
    private Integer quantity;

    EmbeddedInventoryItemModel setId(int id) {
        this.id = id;
        return this;
    }

    EmbeddedInventoryItemModel setName(String name) {
        this.name = name;
        return this;
    }

    EmbeddedInventoryItemModel setWeight(BigDecimal weight) {
        this.weight = weight;
        return this;
    }

    EmbeddedInventoryItemModel setCharges(Integer charges) {
        this.charges = charges;
        return this;
    }

    EmbeddedInventoryItemModel setDetails(String details) {
        this.details = details;
        return this;
    }

    EmbeddedInventoryItemModel setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
