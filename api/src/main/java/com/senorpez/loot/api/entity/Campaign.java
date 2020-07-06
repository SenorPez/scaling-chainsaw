package com.senorpez.loot.api.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @JoinColumn(name = "campaign_id")
    @OneToMany
    private List<Character> characters;

    public Campaign() {
    }

    public Campaign(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Character> getCharacters() {
        return characters;
    }

    public Campaign setCharacters(List<Character> characters) {
        this.characters = characters;
        return this;
    }
}