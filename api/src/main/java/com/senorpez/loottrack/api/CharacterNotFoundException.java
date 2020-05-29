package com.senorpez.loottrack.api;

public class CharacterNotFoundException extends RuntimeException {
    CharacterNotFoundException(final int id) {
        super(String.format("Character with ID of %d not found", id));
    }
}
