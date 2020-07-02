package com.senorpez.loot.api.model;

import com.senorpez.loot.api.CampaignController;
import com.senorpez.loot.api.CharacterController;
import com.senorpez.loot.api.RootController;
import com.senorpez.loot.api.entity.Character;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class CharacterModelAssembler<M extends EmbeddedCharacterModel> extends RepresentationModelAssemblerSupport<Character, M> {
    public CharacterModelAssembler(Class<?> controllerClass, Class<M> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public M toModel(@NonNull Character entity) {
        M model = createModelWithId(entity.getId(), entity, entity.getCampaign().getId());
        model.setId(entity.getId());
        model.setName(entity.getName());
        return model;
    }

    @NonNull
    public CharacterModel toSingleModel(@NonNull Character entity) {
//        return ((CharacterModel) toModel(entity))
//                .setInventory(null)
//                .add(linkTo(CharacterController.class, entity.getCampaign().getId()).withRel("characters"))
//                .add(linkTo(RootController.class).withRel("index"));
        return null;
    }

    @NonNull
    public CollectionModel<M> toCollectionModel(Iterable<? extends Character> entities, final int campaignId) {
        return super.toCollectionModel(entities)
                .add(linkTo(CharacterController.class, campaignId).withSelfRel())
                .add(linkTo(methodOn(CampaignController.class).campaigns(campaignId)).withRel("campaign"))
                .add(linkTo(RootController.class).withRel("index"));
    }
}
