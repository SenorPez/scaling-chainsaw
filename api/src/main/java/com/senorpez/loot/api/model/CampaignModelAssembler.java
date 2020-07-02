package com.senorpez.loot.api.model;

import com.senorpez.loot.api.CampaignController;
import com.senorpez.loot.api.CharacterController;
import com.senorpez.loot.api.RootController;
import com.senorpez.loot.api.entity.Campaign;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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

    @NonNull
    public CampaignModel toSingleModel(@NonNull Campaign entity) {
        return toModel(entity)
                .add(linkTo(CampaignController.class).withRel("campaigns"))
                .add(linkTo(CharacterController.class, entity.getId()).withRel("characters"))
                .add(linkTo(RootController.class).withRel("index"));
    }

    @Override
    @NonNull
    public CollectionModel<CampaignModel> toCollectionModel(@NonNull Iterable<? extends Campaign> entities) {
        return super.toCollectionModel(entities)
                .add(linkTo(CampaignController.class).withSelfRel())
                .add(linkTo(RootController.class).withRel("index"));
    }
}
