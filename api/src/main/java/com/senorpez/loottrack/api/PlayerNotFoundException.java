package com.senorpez.loottrack.api;

public class PlayerNotFoundException extends RuntimeException {
    PlayerNotFoundException(final int id) {
        super(String.format("Player with ID of %d not found", id));
    }
}
