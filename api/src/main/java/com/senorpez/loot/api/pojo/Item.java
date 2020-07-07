package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
class Item {
    private final String name;
    private final BigDecimal weight;

    Item(
            @JsonProperty("name") String name,
            @JsonProperty("weight") BigDecimal weight) {
        this.name = name;
        this.weight = weight;
    }

    String getName() {
        return name;
    }

    BigDecimal getWeight() {
        return weight;
    }
}
