package com.senorpez.loot.api.exception;

public class CampaignNotFoundException extends RuntimeException {
    public CampaignNotFoundException(final int id) {
        super(String.format("Campaign with ID of %d not found", id));
    }
}
