package com.senorpez.loottrack.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

public class PlayerModelAssembler extends RepresentationModelAssemblerSupport<Player, PlayerModel> {
    public PlayerModelAssembler(Class<?> controllerClass, Class<PlayerModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public PlayerModel toModel(Player entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
                .setId(entity.getId())
                .setName(entity.getName())
                .setInventory(entity.getInventory());
    }
}
