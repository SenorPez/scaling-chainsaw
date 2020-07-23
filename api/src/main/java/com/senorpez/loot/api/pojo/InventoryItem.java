package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryItem extends Item {
    private final String details;
    private final Integer charges;

    public InventoryItem(
            @JsonProperty("name") String name,
            @JsonProperty("weight") BigDecimal weight,
            @JsonProperty("charges") Integer charges,
            @JsonProperty("details") String details) {
        super(name, weight);
        this.details = details;
        this.charges = charges;
    }

    public String getDetails() {
        return details;
    }

    public Integer getCharges() {
        return charges;
    }
}
