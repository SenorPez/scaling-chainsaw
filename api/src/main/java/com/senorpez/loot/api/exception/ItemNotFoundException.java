package com.senorpez.loot.api.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(final int id) {
        super(String.format("Item with ID of %d not found", id));
    }
}
