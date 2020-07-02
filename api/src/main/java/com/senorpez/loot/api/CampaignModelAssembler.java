package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Campaign;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

public class CampaignModelAssembler extends RepresentationModelAssemblerSupport<Campaign, CampaignModel> {
    public CampaignModelAssembler(Class<?> controllerClass, Class<CampaignModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public CampaignModel toModel(@NonNull Campaign entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName());
    }
}
