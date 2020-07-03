package com.senorpez.loot.api.model;

import com.senorpez.loot.api.ItemTransactionRepository;
import com.senorpez.loot.api.entity.InventoryItem;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InventoryItemModelAssembler {
    private static InventoryItemModel toModel(@NonNull InventoryItem item, ItemTransactionRepository itemTransactionRepository) {
        final Integer quantity = itemTransactionRepository.getQuantity(item.getId());
        return new InventoryItemModel()
                .setId(item.getId())
                .setName(item.getItem().getName())
                .setWeight(item.getItem().getWeight())
                .setDetails(item.getDetails())
                .setCharges(item.getCharges())
                .setQuantity(quantity);
    }

    static List<InventoryItemModel> toModel(@NonNull List<InventoryItem> items, ItemTransactionRepository itemTransactionRepository) {
        return items.stream()
                .map(item -> toModel(item, itemTransactionRepository))
                .collect(Collectors.toList());
    }
}
