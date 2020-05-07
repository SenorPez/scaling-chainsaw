package com.senorpez.loottrack.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

    private ItemModelAssembler assembler = new ItemModelAssembler(ItemController.class, ItemModel.class);

    @GetMapping
    ResponseEntity<CollectionModel<ItemModel>> items() {
        CollectionModel<ItemModel> itemModels = new CollectionModel<>(
                StreamSupport
                        .stream(itemRepository.findAll().spliterator(), false)
                        .map(assembler::toModel)
                        .collect(Collectors.toList()));
        itemModels.add(linkTo(ItemController.class).withSelfRel());
        itemModels.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(itemModels);
    }

    @GetMapping("/{itemId}")
    ResponseEntity<ItemModel> items(@PathVariable final int itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        ItemModel itemModel = assembler.toModel(item);
        itemModel.add(linkTo(ItemController.class).withRel("items"));
        itemModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.ok(itemModel);
    }

    @PostMapping(consumes = {HAL_JSON_VALUE})
    ResponseEntity<ItemModel> addItem(@RequestBody Item newItem) {
        Item item = itemRepository.save(newItem);
        ItemModel itemModel = assembler.toModel(item);
        itemModel.add(linkTo(ItemController.class).withRel("items"));
        itemModel.add(linkTo(RootController.class).withRel("index"));

        return ResponseEntity.created(itemModel.getRequiredLink("self").toUri()).body(itemModel);
    }
}
