package com.senorpez.loottrack.api;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.stream.Stream;

public class ItemTransactionIdGenerator implements IdentifierGenerator {
    /**
     * Generate a new identifier.
     *
     * @param session The session from which the request originates
     * @param object  the entity or collection (idbag) for which the id is being generated
     * @return a new identifier
     * @throws HibernateException Indicates trouble generating the identifier
     */
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        int characterId = ((ItemTransaction) object).getCharacter().getId();
        int campaignId = ((ItemTransaction) object).getCharacter().getCampaign().getId();
        String query = String.format("SELECT id FROM itemtransactions WHERE campaign_id = %d AND characterId = %d", campaignId, characterId);

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
