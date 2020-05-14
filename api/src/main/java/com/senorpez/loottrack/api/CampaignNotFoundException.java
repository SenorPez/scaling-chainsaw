package com.senorpez.loottrack.api;

public class CampaignNotFoundException extends RuntimeException {
    CampaignNotFoundException(final int id) {
        super(String.format("Campaign with ID of %d not found", id));
    }
}
