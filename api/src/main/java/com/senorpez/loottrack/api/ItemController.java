package com.senorpez.loottrack.api;

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

    private final EmbeddedItemModelAssembler collectionAssembler = new EmbeddedItemModelAssembler(ItemController.class, EmbeddedItemModel.class);
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
        Item updatedItem = item
                .setName(Optional.ofNullable(incomingItem.getName()).orElseGet(item::getName))
                .setWeight(Optional.ofNullable(incomingItem.getWeight()).orElseGet(item::getWeight))
                .setDetails(Optional.ofNullable(incomingItem.getDetails()).orElseGet(item::getDetails))
                .setCharges(Optional.ofNullable(incomingItem.getCharges()).orElseGet(item::getCharges));
        itemRepository.save(updatedItem);

        ItemModel itemModel = assembler.toModel(updatedItem);
        itemModel.add(linkTo(ItemController.class).withRel("lootitems"));
        itemModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(itemModel.getRequiredLink("self").toUri()).body(itemModel);
    }
}
