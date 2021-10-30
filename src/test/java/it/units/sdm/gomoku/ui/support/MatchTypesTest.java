package it.units.sdm.gomoku.ui.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchTypesTest {

    private final MatchTypes cpuVsPerson = MatchTypes.CPU_VS_PERSON;
    private final MatchTypes personVsPerson = MatchTypes.PERSON_VS_PERSON;

    @Test
    void getNumberOfHumanPlayersCPU_VS_PERSON() {
        assertEquals(1, cpuVsPerson.getNumberOfHumanPlayers());
    }

    @Test
    void getNumberOfHumanPlayersPERSON_VS_PERSON() {
        assertEquals(2, personVsPerson.getNumberOfHumanPlayers());
    }

    @Test
    void testToStringCPU_VS_PERSON() {
        assertEquals("CPU VS PERSON", cpuVsPerson.toString());
    }

    @Test
    void testToStringPERSON_VS_PERSON() {
        assertEquals("PERSON VS PERSON", personVsPerson.toString());
    }

    @Test
    void getExposedValueOfCPU_VS_PERSON() {
        assertEquals("1", cpuVsPerson.getExposedValueOf());
    }

    @Test
    void getExposedValueOfPERSON_VS_PERSON() {
        assertEquals("2", personVsPerson.getExposedValueOf());
    }
}