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

    public ItemTransactionModel() {
    }

    public ItemTransactionModel setId(int id) {
        this.id = id;
        return this;
    }

    public ItemTransactionModel setItemName(String itemName) {
        this.itemName = itemName;
        return this;
    }

    public ItemTransactionModel setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }
}
