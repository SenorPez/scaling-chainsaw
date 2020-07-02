package com.senorpez.loot.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

@Relation(value = "campaign", collectionRelation = "campaign")
public class CampaignModel extends RepresentationModel<CampaignModel> {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;

    CampaignModel setId(int id) {
        this.id = id;
        return this;
    }

    CampaignModel setName(String name) {
        this.name = name;
        return this;
    }
}
