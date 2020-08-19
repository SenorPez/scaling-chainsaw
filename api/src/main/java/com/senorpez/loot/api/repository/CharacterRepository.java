package com.senorpez.loot.api.repository;

import com.senorpez.loot.api.entity.CharacterEntity;
import org.springframework.data.repository.CrudRepository;

public interface CharacterRepository extends CrudRepository<CharacterEntity, Integer> {
//    Optional<CharacterEntity> findByCampaignAndId(CampaignEntity campaignEntity, int characterId);
}