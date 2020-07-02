package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Campaign;
import org.springframework.data.repository.CrudRepository;

public interface CampaignRepository extends CrudRepository<Campaign, Integer> {
}
