package com.senorpez.loottrack.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

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
    public CampaignModel toModel(Campaign entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName());
    }
}
