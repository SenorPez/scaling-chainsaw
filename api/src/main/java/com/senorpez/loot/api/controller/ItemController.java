package com.senorpez.loot.api.controller;

import com.senorpez.loot.api.entity.ItemEntity;
import com.senorpez.loot.api.exception.ItemNotFoundException;
import com.senorpez.loot.api.model.EmbeddedItemModel;
import com.senorpez.loot.api.model.EmbeddedItemModelAssembler;
import com.senorpez.loot.api.model.ItemModel;
import com.senorpez.loot.api.model.ItemModelAssembler;
import com.senorpez.loot.api.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;

@RequestMapping(
        value = "/items",
        produces = {HAL_JSON_VALUE}
)
@RestController
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    private final EmbeddedItemModelAssembler collectionAssembler = new EmbeddedItemModelAssembler(ItemController.class, EmbeddedItemModel.class);
    private final ItemModelAssembler assembler = new ItemModelAssembler(ItemController.class, ItemModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<EmbeddedItemModel>> items() {
        final CollectionModel<EmbeddedItemModel> itemModels = collectionAssembler.toCollectionModel(itemRepository.findAll());
        return ResponseEntity.ok(itemModels);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<ItemModel> items(@PathVariable final int itemId) {
        final ItemEntity itemEntity = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        final ItemModel itemModel = assembler.toModel(itemEntity);
        return ResponseEntity.ok(itemModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<ItemModel> addItem(@RequestHeader String Authorization, @RequestBody final ItemEntity newItemEntity) {
        final ItemEntity itemEntity = itemRepository.save(newItemEntity);
        final ItemModel itemModel = assembler.toModel(itemEntity);
        return ResponseEntity.created(itemModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(itemModel);
    }

    @PutMapping(value = "/{itemId}", consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<ItemModel> updateItem(@RequestHeader String Authorization, @PathVariable final int itemId, @RequestBody final ItemEntity incomingItemEntity) {
        final ItemEntity itemEntity = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
//        final ItemEntity updateItemEntity = new ItemEntity(
//                itemEntity.getId(),
//                Optional.of(incomingItemEntity.getName()).orElseGet(itemEntity::getName),
//                Optional.ofNullable(incomingItemEntity.getWeight()).orElseGet(itemEntity::getWeight)
//        );
        final ItemEntity updateItemEntity = null;
        final ItemModel itemModel = assembler.toModel(itemRepository.save(updateItemEntity));
        return ResponseEntity.created(itemModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(itemModel);
    }
}
