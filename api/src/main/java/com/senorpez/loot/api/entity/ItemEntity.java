package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Item;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "items")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(precision = 19, scale = 3)
    private BigDecimal weight;

    public ItemEntity() {
    }

    public ItemEntity(final Item item) {
        setName(item.getName());
        setWeight(item.getWeight());
    }

    private void setName(final String name) {
        if (name == null) throw new IllegalArgumentException("Name must not be null");
        this.name = name;
    }

    private void setWeight(final BigDecimal weight) {
        this.weight = weight;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
