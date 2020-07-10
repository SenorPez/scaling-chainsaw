package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public
class Campaign {
    private final String name;

    public Campaign(
            @JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
