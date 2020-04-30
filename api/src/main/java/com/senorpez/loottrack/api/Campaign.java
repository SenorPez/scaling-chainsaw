package com.senorpez.loottrack.api;

class Campaign {
    private final int id;
    private final String name;

    Campaign(int id, String name) {
        this.id = id;
        this.name = name;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
