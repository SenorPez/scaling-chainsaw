package com.senorpez.loot.api;

class ItemNotFoundException extends RuntimeException {
    ItemNotFoundException(final int id) {
        super(String.format("Item with ID of %d not found", id));
    }
}
