package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Campaign;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampaignEntityTest {
    @Test
    void create_WithName() {
        final String expectedName = "Defiance in Phlan";
        final Campaign campaign = new Campaign(expectedName);
        final CampaignEntity campaignEntity = new CampaignEntity(campaign);
        assertThat(campaignEntity.getId(), nullValue());
        assertThat(campaignEntity.getName(), is(expectedName));
        assertThat(campaignEntity.getCharacterEntities(), nullValue());
    }

    @Test
    void create_NullName() {
        final Campaign campaign = new Campaign(null);
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CampaignEntity(campaign));
        final Class<IllegalArgumentException> expectedValue = IllegalArgumentException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }
}