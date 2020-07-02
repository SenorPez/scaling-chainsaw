package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.InventoryItem;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

class InventoryItemTemplate implements Comparable<InventoryItemTemplate> {
    public final int quantity;
    public final String name;
    public final BigDecimal weight;
    public final String details;
    public final Integer charges;

    InventoryItemTemplate(InventoryItem inventoryItem) {
        this.quantity = inventoryItem.getQuantity();
        this.name = inventoryItem.getItem().getName();
        this.weight = inventoryItem.getItem().getWeight();
        this.details = inventoryItem.getDetails();
        this.charges = inventoryItem.getCharges();
    }

    @Override
    public int compareTo(@NonNull InventoryItemTemplate other) {
        if (this.name.equalsIgnoreCase("Copper Piece")) {
            return 1;
        } else if (other.name.equalsIgnoreCase("Copper Piece")) {
            return -1;
        }

        if (this.name.equalsIgnoreCase("Silver Piece")
                && !other.name.equalsIgnoreCase("Copper Piece")
        ) {
            return 1;
        } else if (other.name.equalsIgnoreCase("Silver Piece")
                && !this.name.equalsIgnoreCase("Copper Piece")) {
            return -1;
        }

        if (this.name.equalsIgnoreCase("Gold Piece")
                && !(
                other.name.equalsIgnoreCase("Silver Piece") ||
                        other.name.equalsIgnoreCase("Copper Piece")
        )) {
            return 1;
        } else if (other.name.equalsIgnoreCase("Gold Piece")
                && !(
                this.name.equalsIgnoreCase("Silver Piece") ||
                        this.name.equalsIgnoreCase("Copper Piece")
        )) {
            return -1;
        }

        if (this.name.equalsIgnoreCase("Platinum Piece")
                && !(
                other.name.equalsIgnoreCase("Gold Piece") ||
                        other.name.equalsIgnoreCase("Silver Piece") ||
                        other.name.equalsIgnoreCase("Copper Piece")
        )) {
            return 1;
        } else if (other.name.equalsIgnoreCase("Platinum Piece")
                && !(
                this.name.equalsIgnoreCase("Gold Piece") ||
                        this.name.equalsIgnoreCase("Silver Piece") ||
                        this.name.equalsIgnoreCase("Copper Piece")
        )) {
            return -1;
        }

        return this.name.compareToIgnoreCase(other.name);
    }
}
