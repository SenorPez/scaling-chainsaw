package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Character;
import com.senorpez.loot.api.model.CharacterModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.lang.NonNull;

import java.util.List;

public class CharacterModelAssembler extends RepresentationModelAssemblerSupport<Character, CharacterModel> {
    public CharacterModelAssembler(Class<?> controllerClass, Class<CharacterModel> resourceType) {
        super(controllerClass, resourceType);
    }

    @Override
    @NonNull
    public CharacterModel toModel(@NonNull Character entity) {
        return new CharacterModel();
//        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
//                .setId(entity.getId())
//                .setName(entity.getName())
//                .setInventory(Collections.emptyList());
    }

    public CharacterModel toModel(@NonNull Character entity, List<Object[]> inventory) {
        return new CharacterModel();
//        return createModelWithId(entity.getId(), entity, entity.getCampaign().getId())
//                .setId(entity.getId())
//                .setName(entity.getName());
//                .setInventoryFromDatabase(inventory);
    }
}
