package com.senorpez.loot.api;

import com.senorpez.loot.api.entity.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemRepository extends CrudRepository<Item, Integer> {
}
