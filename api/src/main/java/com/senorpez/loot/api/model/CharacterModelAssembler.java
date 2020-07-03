package com.senorpez.loot.api.model;

import com.senorpez.loot.api.CharacterController;
import com.senorpez.loot.api.RootController;
import com.senorpez.loot.api.entity.Character;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
                .setInventory(null)
                .add(linkTo(CharacterController.class).withRel("characters"))
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }
}
