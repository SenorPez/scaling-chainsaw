package com.senorpez.loot.api.model;

import com.senorpez.loot.api.Application;
import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.CharacterEntity;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class CharacterModelAssembler extends RepresentationModelAssemblerSupport<CharacterEntity, CharacterModel> {
    public CharacterModelAssembler(Class<?> controllerClass, Class<CharacterModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public CharacterModel toModel(@NonNull CharacterEntity entity) {
        final EmbeddedInventoryItemModelAssembler inventoryItemModelAssembler = new EmbeddedInventoryItemModelAssembler();

        CharacterModel characterModel = createModelWithId(entity.getId(), entity, entity.getCampaignEntity().getId())
                .setId(entity.getId())
                .setName(entity.getName())
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX))
                .add(linkTo(CharacterController.class, entity.getCampaignEntity().getId()).withRel("characters"));

        entity.getItems()
                .forEach(inventoryItemEntity -> {
                    EmbeddedInventoryItemModel inventoryItemModel = inventoryItemModelAssembler.toModel(inventoryItemEntity, Application.itemTransactionRepository.getQuantity(inventoryItemEntity.getId()));
                    characterModel.inventory.add(inventoryItemModel);
                });

        return characterModel;
    }
}
