package com.senorpez.loottrack.api;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface PlayerRepository extends CrudRepository<Player, Integer> {
    List<Player> findByCampaign(Campaign campaign);

    Optional<Player> findByCampaignAndId(Campaign campaign, int playerId);
}