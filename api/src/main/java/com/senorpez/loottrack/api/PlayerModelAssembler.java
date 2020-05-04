package com.senorpez.loottrack.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class PlayerModelAssembler extends RepresentationModelAssemblerSupport<Player, PlayerModel> {
    /**
     * Creates a new {@link RepresentationModelAssemblerSupport} using the given controller class and resource type.
     *
     * @param controllerClass must not be {@literal null}.
     * @param resourceType    must not be {@literal null}.
     */
    public PlayerModelAssembler(Class<?> controllerClass, Class<PlayerModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public PlayerModel toModel(Player entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaignId())
                .setId(entity.getId())
                .setName(entity.getName());
    }
}
