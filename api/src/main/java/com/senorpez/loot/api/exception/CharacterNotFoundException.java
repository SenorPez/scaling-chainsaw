package com.senorpez.loot.api.exception;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException(final int id) {
        super(String.format("Character with ID of %d not found", id));
    }
}
