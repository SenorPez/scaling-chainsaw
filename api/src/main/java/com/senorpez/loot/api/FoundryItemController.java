package com.senorpez.loot.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(
        value = "/foundryitems",
        produces = {MediaType.APPLICATION_JSON_VALUE}
)
@RestController
public class FoundryItemController {
    @Autowired
    private FoundryItemRepository foundryItemRepository;

    @CrossOrigin
    @PostMapping
    ResponseEntity<FoundryItem> addItem(@RequestBody final FoundryItem newFoundryItem) {
        FoundryItem foundryItem = foundryItemRepository.save(newFoundryItem);
        return ResponseEntity.ok(foundryItem);
    }

    @CrossOrigin
    @DeleteMapping
    ResponseEntity<FoundryItem> deleteItem(@RequestBody final FoundryItem deleteFoundryItem) {
        foundryItemRepository.delete(deleteFoundryItem);
        return ResponseEntity.ok(new FoundryItem());
    }
}
