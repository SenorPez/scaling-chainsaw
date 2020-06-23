package com.senorpez.loot.api;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ItemTransactionRepository extends CrudRepository<ItemTransaction, Integer> {
    @Query(value = "SELECT `item_id`, SUM(`quantity`) AS quantity, `name`, `weight`, `details`, `charges` " +
            "FROM `itemtransactions` " +
            "LEFT JOIN `items` ON `items`.`id` = `itemtransactions`.`item_id` " +
            "WHERE `character_id` = :characterId AND `campaign_id` = :campaignId " +
            "GROUP BY `item_id` " +
            "HAVING SUM(`quantity`) <> 0", nativeQuery = true)
    List<Object[]> getInventory(@Param("characterId") int characterId, @Param("campaignId") int campaignId);
}
