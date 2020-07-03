package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Item;
import com.senorpez.loot.api.model.EmbeddedItemModel;
import com.senorpez.loot.api.model.EmbeddedItemModelAssembler;
import com.senorpez.loot.api.model.ItemModel;
import com.senorpez.loot.api.model.ItemModelAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.Optional;

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
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        final ItemModel itemModel = assembler.toModel(item);
        return ResponseEntity.ok(itemModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<ItemModel> addItem(@RequestHeader String Authorization, @RequestBody final Item newItem) {
        final Item item = itemRepository.save(newItem);
        final ItemModel itemModel = assembler.toModel(item);
        return ResponseEntity.created(itemModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(itemModel);
    }

    @PutMapping(value = "/{itemId}", consumes = {HAL_JSON_VALUE})
    @RolesAllowed("user")
    ResponseEntity<ItemModel> updateItem(@RequestHeader String Authorization, @PathVariable final int itemId, @RequestBody final Item incomingItem) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        final Item updateItem = new Item(
                item.getId(),
                Optional.of(incomingItem.getName()).orElseGet(item::getName),
                Optional.ofNullable(incomingItem.getWeight()).orElseGet(item::getWeight)
        );
        final ItemModel itemModel = assembler.toModel(itemRepository.save(updateItem));
        return ResponseEntity.created(itemModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(itemModel);
    }
}
