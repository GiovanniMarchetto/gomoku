package it.units.sdm.gomoku.ui.support;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class MatchTypesTest {

    @Test
    void getNumberOfHumanPlayersCPU_VS_PERSON() {
        MatchTypes matchTypes = MatchTypes.CPU_VS_PERSON;
        assertEquals(1,matchTypes.getNumberOfHumanPlayers());
    }

    @Test
    void getNumberOfHumanPlayersPERSON_VS_PERSON() {
        MatchTypes matchTypes = MatchTypes.PERSON_VS_PERSON;
        assertEquals(2,matchTypes.getNumberOfHumanPlayers());
    }

    @Test
    void testToStringCPU_VS_PERSON() {
        MatchTypes matchTypes = MatchTypes.CPU_VS_PERSON;
        assertEquals("CPU VS PERSON",matchTypes.toString());
    }

    @Test
    void testToStringPERSON_VS_PERSON() {
        MatchTypes matchTypes = MatchTypes.PERSON_VS_PERSON;
        assertEquals("PERSON VS PERSON",matchTypes.toString());
    }

    @Test
    void getExposedValueOfCPU_VS_PERSON() {
        MatchTypes matchTypes = MatchTypes.CPU_VS_PERSON;
        assertEquals("1",matchTypes.getExposedValueOf());
    }

    @Test
    void getExposedValueOfPERSON_VS_PERSON() {
        MatchTypes matchTypes = MatchTypes.PERSON_VS_PERSON;
        assertEquals("2",matchTypes.getExposedValueOf());
    }
}