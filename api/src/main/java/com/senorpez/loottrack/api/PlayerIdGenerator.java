package com.senorpez.loottrack.api;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.stream.Stream;

public class PlayerIdGenerator implements IdentifierGenerator {
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        int campaignId = ((Player) object).getCampaign().getId();
        String query = String.format("SELECT id FROM players WHERE campaign_id = %d", campaignId);

        Stream<?> stream = session
                .createNativeQuery(query)
                .stream();

        int max = stream
                .mapToInt(value -> (Integer) value)
                .max()
                .orElse(0);

        return max + 1;
    }
}
