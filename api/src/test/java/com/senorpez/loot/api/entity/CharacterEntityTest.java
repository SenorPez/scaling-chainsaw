package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Character;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterEntityTest {
    @Test
    void create_WithName_WithCampaignEntity() {
        final String expectedName = "Aethelwuf";
        final CampaignEntity campaignEntity = new CampaignEntity();
        final Character character = new Character(expectedName);
        final CharacterEntity characterEntity = new CharacterEntity(character, campaignEntity);
        assertThat(characterEntity.getId(), nullValue());
        assertThat(characterEntity.getName(), is(expectedName));
        assertThat(characterEntity.getCampaignEntity(), nullValue());
        assertThat(characterEntity.getItems(), nullValue());
    }

    @Test
    void create_NullName() {
        final CampaignEntity campaignEntity = new CampaignEntity();
        final Character character = new Character(null);
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CharacterEntity(character, campaignEntity));
        final Class<IllegalArgumentException> expectedValue = IllegalArgumentException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }

    @Test
    void create_NullCampaignEntity() {
        final Character character = new Character(null);
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CharacterEntity(character, null));
        final Class<IllegalArgumentException> expectedValue = IllegalArgumentException.class;
        assertThat(exception.getClass(), is(expectedValue));
    }
}