package com.senorpez.loot.api.model;

import com.senorpez.loot.api.controller.CampaignController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.CampaignEntity;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

class EmbeddedCampaignModelAssembler extends RepresentationModelAssemblerSupport<CampaignEntity, EmbeddedCampaignModel> {
    EmbeddedCampaignModelAssembler(Class<?> controllerClass, Class<EmbeddedCampaignModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    public EmbeddedCampaignModel toModel(CampaignEntity entity) {
        return createModelWithId(entity.getId(), entity)
                .setId(entity.getId())
                .setName(entity.getName());
    }

    @Override
    public CollectionModel<EmbeddedCampaignModel> toCollectionModel(Iterable<? extends CampaignEntity> entities) {
        return super.toCollectionModel(entities)
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX))
                .add(linkTo(CampaignController.class).withRel(IanaLinkRelations.SELF));
    }
}
