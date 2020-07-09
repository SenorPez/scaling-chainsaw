package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class ItemTransaction {
    private final Integer item_id;
    private final Integer quantity;
    private final String remark;

    ItemTransaction(
            @JsonProperty("item_id") Integer item_id,
            @JsonProperty("quantity") Integer quantity,
            @JsonProperty("remark") String remark) {
        this.item_id = item_id;
        this.quantity = quantity;
        this.remark = remark;
    }

    public Integer getItem_id() {
        return item_id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getRemark() {
        return remark;
    }
}
