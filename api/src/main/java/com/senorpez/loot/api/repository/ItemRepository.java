package com.senorpez.loot.api.repository;

import com.senorpez.loot.api.entity.ItemEntity;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<ItemEntity, Integer> {
}
