package com.senorpez.loot.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
class Character {
    private final String name;

    Character(
            @JsonProperty("name") String name) {
        this.name = name;
    }

    String getName() {
        return name;
    }
}
