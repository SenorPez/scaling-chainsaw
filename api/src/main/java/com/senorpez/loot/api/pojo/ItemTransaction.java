package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class ItemTransaction {
    private final Integer inventoryItemId;
    private final Integer quantity;
    private final String remark;

    ItemTransaction(
            @JsonProperty("inv_item_id") Integer inventoryItemId,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("remark") String remark) {
        this.inventoryItemId = inventoryItemId;
        this.quantity = quantity;
        this.remark = remark;
    }

    public Integer getInventoryItemId() {
        return inventoryItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getRemark() {
        return remark;
    }
}
