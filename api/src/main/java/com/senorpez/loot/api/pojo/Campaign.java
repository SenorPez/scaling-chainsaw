package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class Campaign {
    private final String name;

    Campaign(
            @JsonProperty("name") String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
