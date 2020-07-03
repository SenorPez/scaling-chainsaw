package com.senorpez.loot.api.model;

import com.senorpez.loot.api.CampaignController;
import com.senorpez.loot.api.CharacterController;
import com.senorpez.loot.api.RootController;
import com.senorpez.loot.api.entity.Character;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class EmbeddedCharacterModelAssembler extends RepresentationModelAssemblerSupport<Character, EmbeddedCharacterModel> {
    public EmbeddedCharacterModelAssembler(Class<?> controllerClass, Class<EmbeddedCharacterModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public EmbeddedCharacterModel toModel(@NonNull Character entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
                .setId(entity.getId())
                .setName(entity.getName());
    }

    public CollectionModel<EmbeddedCharacterModel> toCollectionModel(Iterable<? extends Character> entities, final int campaignId) {
        return super.toCollectionModel(entities)
                .add(linkTo(CharacterController.class, campaignId).withRel("character"))
                .add(linkTo(methodOn(CampaignController.class).campaigns(campaignId)).withRel("campaign"))
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }
}
