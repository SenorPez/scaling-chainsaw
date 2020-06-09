package com.senorpez.loot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RequestMapping(
        value = "/items",
        produces = {HAL_JSON_VALUE}
)
@RestController
public class ItemController {
    @Autowired
    private ItemRepository itemRepository;

    private final EmbeddedItemModelAssembler collectionAssembler = new EmbeddedItemModelAssembler();
    private final ItemModelAssembler assembler = new ItemModelAssembler(ItemController.class, ItemModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<EmbeddedItemModel>> items() {
        CollectionModel<EmbeddedItemModel> itemModels = new CollectionModel<>(
                collectionAssembler.toCollectionModel(itemRepository.findAll())
        );
        itemModels.add(linkTo(ItemController.class).withSelfRel());
        itemModels.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(itemModels);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<ItemModel> items(@PathVariable final int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        ItemModel itemModel = assembler.toModel(item);
        itemModel.add(linkTo(ItemController.class).withRel("lootitems"));
        itemModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(itemModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<ItemModel> addItem(@RequestHeader String Authorization, @RequestBody final Item newItem) {
        Item item = itemRepository.save(newItem);
        ItemModel itemModel = assembler.toModel(item);
        itemModel.add(linkTo(ItemController.class).withRel("lootitems"));
        itemModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(itemModel.getRequiredLink("self").toUri()).body(itemModel);
    }

    @PutMapping(value = "/{itemId}", consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<ItemModel> updateItem(@RequestHeader String Authorization, @PathVariable final int itemId, @RequestBody final Item incomingItem) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        Item updatedItem = new Item(
                item.getId(),
                Optional.ofNullable(incomingItem.getName()).orElseGet(item::getName),
                Optional.ofNullable(incomingItem.getWeight()).orElseGet(item::getWeight),
                Optional.ofNullable(incomingItem.getDetails()).orElseGet(item::getDetails),
                Optional.ofNullable(incomingItem.getCharges()).orElseGet(item::getCharges)
        );

        ItemModel itemModel = assembler.toModel(itemRepository.save(updatedItem));
        itemModel.add(linkTo(ItemController.class).withRel("lootitems"));
        itemModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(itemModel.getRequiredLink("self").toUri()).body(itemModel);
    }
}
