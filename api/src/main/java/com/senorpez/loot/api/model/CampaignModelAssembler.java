package com.senorpez.loot.api.model;

import com.senorpez.loot.api.controller.CampaignController;
import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.CampaignEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class CampaignModelAssembler extends RepresentationModelAssemblerSupport<CampaignEntity, CampaignModel> {
    public CampaignModelAssembler(Class<?> controllerClass, Class<CampaignModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public CampaignModel toModel(@NonNull CampaignEntity entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @NonNull
    public CampaignModel toSingleModel(@NonNull CampaignEntity entity) {
        return toModel(entity)
                .add(linkTo(CharacterController.class, entity.getId()).withRel("characters"))
                .add(linkTo(CampaignController.class).withRel("campaigns"))
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }

    @Override
    @NonNull
    public CollectionModel<CampaignModel> toCollectionModel(@NonNull Iterable<? extends CampaignEntity> entities) {
        return super.toCollectionModel(entities)
                .add(linkTo(CampaignController.class).withSelfRel())
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }
}
