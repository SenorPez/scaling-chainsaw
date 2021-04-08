package com.senorpez.loot.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "foundryitems")
@JsonDeserialize(using = FoundryItemDeserializer.class)
class FoundryItem {
    @Id
    private String itemId;

    private String actorId;

    public FoundryItem() {
    }

    public FoundryItem(String actorId, String itemId) {
        this.actorId = actorId;
        this.itemId = itemId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getActorId() {
        return actorId;
    }
}
