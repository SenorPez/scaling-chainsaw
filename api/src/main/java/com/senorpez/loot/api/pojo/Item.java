package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Item {
    private final String name;
    private final BigDecimal weight;

    public Item(
            @JsonProperty("name") String name,
            @JsonProperty("weight") BigDecimal weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
