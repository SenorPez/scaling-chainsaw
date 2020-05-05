package com.senorpez.loottrack.api;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface PlayerRepository extends CrudRepository<Player, Integer> {
    List<Player> findByCampaign(Campaign campaign);

    Player findByCampaignAndId(Campaign campaign, int playerId);
}