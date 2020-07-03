package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.ItemTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemTransactionRepository extends CrudRepository<ItemTransaction, Integer> {
    @Query(value = "SELECT `inventoryitems`.`id`, `name`, `weight`, SUM(`quantity`) AS quantity, `details`, `charges` " +
            "FROM `itemtransactions` " +
            "LEFT JOIN `inventoryitems` ON `inventoryitems`.`id` = `itemtransactions`.`item_id` " +
            "LEFT JOIN `items` ON `items`.`id` = `inventoryitems`.`item_id` " +
            "WHERE `inventoryitems`.`character_id` = :characterId " +
            "GROUP BY `inventoryitems`.`id` " +
            "HAVING SUM(`quantity`) <> 0", nativeQuery = true)
    List<Object[]> getInventory(@Param("characterId") int characterId);

    @Query(value = "SELECT SUM(`quantity`) AS quantity " +
            "FROM `itemtransactions` " +
            "WHERE `item_id` = :itemId " +
            "HAVING SUM(`quantity`) <> 0", nativeQuery = true)
    Integer getQuantity(@Param("itemId") final int itemId);
}