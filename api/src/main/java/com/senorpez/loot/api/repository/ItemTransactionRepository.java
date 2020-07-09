package com.senorpez.loot.api.repository;

import com.senorpez.loot.api.entity.ItemTransactionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ItemTransactionRepository extends CrudRepository<ItemTransactionEntity, Integer> {
    @Query(value = "SELECT SUM(`quantity`) AS quantity " +
            "FROM `itemtransactions` " +
            "WHERE `item_id` = :itemId " +
            "HAVING SUM(`quantity`) <> 0", nativeQuery = true)
    Integer getQuantity(@Param("itemId") final int itemId);
}