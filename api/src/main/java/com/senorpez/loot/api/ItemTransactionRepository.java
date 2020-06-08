package com.senorpez.loot.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface ItemTransactionRepository extends CrudRepository<ItemTransaction, Integer> {
    @Query(value = "SELECT name AS name, SUM(quantity) AS quantity, charges " +
            "FROM itemtransactions " +
            "LEFT JOIN items ON items.id = itemtransactions.item_id " +
            "WHERE character_id = ?1 AND campaign_id = ?2 " +
            "GROUP BY item_id " +
            "HAVING SUM(quantity) <> 0", nativeQuery = true)
    List<Object[]> getInventory(int characterId, int campaignId);
}
