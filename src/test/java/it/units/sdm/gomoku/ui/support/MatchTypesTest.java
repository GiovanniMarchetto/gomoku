package it.units.sdm.gomoku.ui.support;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MatchTypesTest {

    private final MatchTypes cpuVsCpu = MatchTypes.CPU_VS_CPU;
    private final MatchTypes personVsCpu = MatchTypes.PERSON_VS_CPU;
    private final MatchTypes personVsPerson = MatchTypes.PERSON_VS_PERSON;

    @Test
    void getNumberOfHumanPlayersCPU_VS_CPU() {
        assertEquals(0, cpuVsCpu.getNumberOfHumanPlayers());
    }

    @Test
    void getNumberOfHumanPlayersPERSON_VS_CPU() {
        assertEquals(1, personVsCpu.getNumberOfHumanPlayers());
    }

    @Test
    void getNumberOfHumanPlayersPERSON_VS_PERSON() {
        assertEquals(2, personVsPerson.getNumberOfHumanPlayers());
    }

    @Test
    void testToStringCPU_VS_CPU() {
        assertEquals("CPU VS CPU", cpuVsCpu.toString());
    }

    @Test
    void testToStringPERSON_VS_CPU() {
        assertEquals("PERSON VS CPU", personVsCpu.toString());
    }

    @Test
    void testToStringPERSON_VS_PERSON() {
        assertEquals("PERSON VS PERSON", personVsPerson.toString());
    }

    @Test
    void getExposedValueOfCPU_VS_CPU() {
        assertEquals("0", cpuVsCpu.getExposedValueOf());
    }

    @Test
    void getExposedValueOfPERSON_VS_CPU() {
        assertEquals("1", personVsCpu.getExposedValueOf());
    }

    @Test
    void getExposedValueOfPERSON_VS_PERSON() {
        assertEquals("2", personVsPerson.getExposedValueOf());
    }
}