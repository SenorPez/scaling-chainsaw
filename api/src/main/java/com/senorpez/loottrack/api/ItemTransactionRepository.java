package com.senorpez.loottrack.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ItemTransactionRepository extends CrudRepository<ItemTransaction, Integer> {
    @Query(value = "SELECT name AS name, SUM(quantity) AS quantity " +
            "FROM itemtransactions " +
            "LEFT JOIN items ON itemtransactions.item_id = items.id " +
            "WHERE player_id = ?1 AND campaign_id = ?2 " +
            "GROUP BY item_id", nativeQuery = true)
    List<Object[]> getInventory(int playerId, int campaignId);
}
