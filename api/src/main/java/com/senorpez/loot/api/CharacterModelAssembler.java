package com.senorpez.loot.api;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

public class CharacterModelAssembler extends RepresentationModelAssemblerSupport<Character, CharacterModel> {
    public CharacterModelAssembler(Class<?> controllerClass, Class<CharacterModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public CharacterModel toModel(@NonNull Character entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
                .setId(entity.getId())
                .setName(entity.getName())
                .setInventory(entity.getInventory());
    }
}
