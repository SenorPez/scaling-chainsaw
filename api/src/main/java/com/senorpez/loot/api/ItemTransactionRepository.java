package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.ItemTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface ItemTransactionRepository extends CrudRepository<ItemTransaction, Integer> {
    @Query(value = "SELECT `itemtransactions`.`item_id`, `name`, `weight`, SUM(`quantity`) AS quantity, `details`, `charges` " +
            "FROM `itemtransactions` " +
            "LEFT JOIN `inventoryitems` ON `inventoryitems`.`id` = `itemtransactions`.`item_id` " +
            "LEFT JOIN `items` ON `items`.`id` = `inventoryitems`.`item_id` " +
            "WHERE `itemtransactions`.`character_id` = :characterId " +
            "GROUP BY `itemtransactions`.`item_id` " +
            "HAVING SUM(`quantity`) <> 0", nativeQuery = true)
    List<Object[]> getInventory(@Param("campaignId") int campaignId, @Param("characterId") int characterId);
}