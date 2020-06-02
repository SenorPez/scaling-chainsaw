package com.senorpez.loot.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "itemtransaction", collectionRelation = "itemtransaction")
public class ItemTransactionModel extends RepresentationModel<ItemTransactionModel> {
    @JsonProperty
    int id;
    @JsonProperty
    String itemName;
    @JsonProperty
    int quantity;

    ItemTransactionModel() {
    }

    ItemTransactionModel setId(int id) {
        this.id = id;
        return this;
    }

    ItemTransactionModel setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    ItemTransactionModel setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
}
