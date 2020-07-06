package com.senorpez.loot.api.repository;

import com.senorpez.loot.api.entity.Campaign;
import com.senorpez.loot.api.entity.Character;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends CrudRepository<Character, Integer> {
    List<Character> findByCampaign(Campaign campaign);
    Optional<Character> findByCampaignAndId(Campaign campaign, int characterId);
}