package com.senorpez.loot.api.entity;

import com.senorpez.loot.api.pojo.Character;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CharacterEntityTest {
    @Test
    void create_withName_withCampaign() {
        final String expectedName = "Aethelwuf";
        final CampaignEntity expectedCampaign = new CampaignEntity();
        final Character character = new Character(expectedName);
        final CharacterEntity characterEntity = new CharacterEntity(character, expectedCampaign);

        assertThat(characterEntity.getId(), nullValue());
        assertThat(characterEntity.getName(), is(expectedName));
        assertThat(characterEntity.getCampaignEntity(), is(expectedCampaign));
        assertThat(characterEntity.getItems(), is(Collections.emptySet()));
    }

    @Test
    void create_nullName_withCampaign() {
        final CampaignEntity expectedCampaign = new CampaignEntity();
        final Character character = new Character(null);
        final Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CharacterEntity(character, expectedCampaign)
        );
        final Class<IllegalArgumentException> expectedException = IllegalArgumentException.class;
        assertThat(exception.getClass(), is(expectedException));
    }

    @Test
    void create_withName_null_Campaign() {
        final String expectedName = "Aethelwuf";
        final Character character = new Character(expectedName);
        final Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> new CharacterEntity(character, null)
        );
        final Class<IllegalArgumentException> expectedException = IllegalArgumentException.class;
        assertThat(exception.getClass(), is(expectedException));
    }
}