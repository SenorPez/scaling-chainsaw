package com.senorpez.loot.api.model;

import com.senorpez.loot.api.ItemTransactionRepository;
import com.senorpez.loot.api.controller.CharacterController;
import com.senorpez.loot.api.controller.RootController;
import com.senorpez.loot.api.entity.Character;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import java.util.List;

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
                .add(linkTo(CharacterController.class, entity.getCampaign().getId()).withRel("characters"))
                .add(linkTo(RootController.class).withRel(IanaLinkRelations.INDEX));
    }

    public CharacterModel toModel(@NonNull Character entity, ItemTransactionRepository itemTransactionRepository) {
        List<InventoryItemModel> itemModels = InventoryItemModelAssembler.toModel(entity.getItems(), itemTransactionRepository);
        return toModel(entity).setInventory(itemModels);
    }
}
