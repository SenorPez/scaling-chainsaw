package com.senorpez.loot.api.repository;

import com.senorpez.loot.api.entity.Campaign;
import com.senorpez.loot.api.entity.Character;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CharacterRepository extends CrudRepository<Character, Integer> {
    Optional<Character> findByCampaignAndId(Campaign campaign, int characterId);
}