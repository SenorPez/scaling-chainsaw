package com.senorpez.loot.api.model;

import com.senorpez.loot.api.controller.CampaignController;
import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.CharacterEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class EmbeddedCharacterModelAssembler extends RepresentationModelAssemblerSupport<CharacterEntity, EmbeddedCharacterModel> {
    public EmbeddedCharacterModelAssembler(Class<?> controllerClass, Class<EmbeddedCharacterModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public EmbeddedCharacterModel toModel(@NonNull CharacterEntity entity) {
        return createModelWithId(entity.getId(), entity, entity.getCampaignEntity().getId())
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @NonNull
    public CollectionModel<EmbeddedCharacterModel> toCollectionModel(@NonNull Iterable<? extends CharacterEntity> entities, final int campaignId) {
        return super.toCollectionModel(entities)
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX))
                .add(linkTo(CharacterController.class, campaignId).withRel(IanaLinkRelations.SELF))
                .add(linkTo(methodOn(CampaignController.class).campaigns(campaignId)).withRel("campaign"));
    }
}
