package com.senorpez.loot.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

public class CampaignModelAssembler extends RepresentationModelAssemblerSupport<Campaign, CampaignModel> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass must not be {@literal null}.
     * @param resourceType    must not be {@literal null}.
     */
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
