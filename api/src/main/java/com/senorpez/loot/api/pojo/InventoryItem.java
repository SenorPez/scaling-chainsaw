package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItem {
    private final Integer itemId;
    private final Integer charges;
    private final String details;

    public InventoryItem(
            @JsonProperty("item_id") final Integer itemId,
            @JsonProperty("charges") final Integer charges,
            @JsonProperty("details") final String details) {
        this.itemId = itemId;
        this.charges = charges;
        this.details = details;
    }

    public Integer getItemId() {
        return itemId;
    }

    public Integer getCharges() {
        return charges;
    }

    public String getDetails() {
        return details;
    }
}
