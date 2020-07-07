package com.senorpez.loot.api.model;

import com.senorpez.loot.api.entity.InventoryItem;
import com.senorpez.loot.api.repository.ItemTransactionRepository;
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
                .setName(item.getItemEntity().getName())
                .setWeight(item.getItemEntity().getWeight())
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
