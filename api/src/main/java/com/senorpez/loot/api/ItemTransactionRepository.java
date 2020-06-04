package com.senorpez.loot.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ItemTransactionRepository extends CrudRepository<ItemTransaction, Integer> {
    @Query(value = "SELECT SUM(quantity) AS quantity, name, weight, details, charges " +
            "FROM itemtransactions " +
            "LEFT JOIN items ON items.id = itemtransactions.item_id " +
            "WHERE character_id = ?1 AND campaign_id = ?2 " +
            "GROUP BY item_id", nativeQuery = true)
    List<Object[]> getInventory(int characterId, int campaignId);
}
