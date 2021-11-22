package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private static final Stone blackStone = new Stone(Color.BLACK);
    private static final Stone whiteStone = new Stone(Color.WHITE);
    private static final Cell emptyCell = new Cell();
    private static final Cell blackCell = new Cell();
    private static final Cell whiteCell = new Cell();
    private static final List<Cell> cellList = Arrays.asList(
            emptyCell, emptyCell, blackCell, blackCell, whiteCell, blackCell, blackCell,
            whiteCell, whiteCell, whiteCell, whiteCell, whiteCell, emptyCell, emptyCell);
    private Cell cell;

    @BeforeAll
    static void setCellList() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, blackCell);
        TestUtility.setFieldValue("stone", whiteStone, whiteCell);
    }

    @BeforeEach
    void setUp() {
        cell = new Cell();
    }

    @Test
    void createEmptyInstance() {
        assertTrue(cell.isEmpty());
    }

    @Test
    void createCopyInstance() {
        cell.setStoneFromColor(blackStone.getColor());
        Cell cell2 = new Cell(cell);
        assertEquals(cell, cell2);
    }

    @Test
    void testStoneGetter() throws NoSuchFieldException, IllegalAccessException {
        assert cell.getStone() == null;
        TestUtility.setFieldValue("stone", blackCell.getStone(), cell);
        assert cell.getStone() != null;
        assertEquals(blackCell.getStone(), cell.getStone());
    }

    @Test
    void testStoneSetter() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals(blackStone, cell.getStone());
    }

    @Test
    void testIsEmpty() {
        assertEquals(cell.getStone() == null, cell.isEmpty());
    }

    @Test
    void testIsNotEmpty() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertFalse(cell.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
        // TODO:re see this (don't simply delete todo please!)
    void testIfCellBelongsToChainOfTwo(int N) throws NoSuchFieldException, IllegalAccessException {
        final int SIZE_OF_DESIRED_CHAIN = 2;
        NonNegativeInteger n = new NonNegativeInteger(N);
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals(N <= SIZE_OF_DESIRED_CHAIN, cell.isBelongingToChainOfNCellsInList(n, cellList));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
        // TODO:re see this (don't simply delete todo please!)
    void testIfCellBelongsToChainOfFive(int N) throws NoSuchFieldException, IllegalAccessException {
        final int SIZE_OF_DESIRED_CHAIN = 5;
        NonNegativeInteger n = new NonNegativeInteger(N);
        TestUtility.setFieldValue("stone", whiteStone, cell);
        assertEquals(N <= SIZE_OF_DESIRED_CHAIN, cell.isBelongingToChainOfNCellsInList(n, cellList));
    }

    //region toString
    @Test
    void testToStringForEmpty() {
        assertEquals(" ", cell.toString());
    }

    @Test
    void testToStringForBlack() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals("X", cell.toString());
    }

    @Test
    void testToStringForWhite() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", whiteStone, cell);
        assertEquals("O", cell.toString());
    }
    //endregion

    //region equals and hashCode
    @Test
    void equalsToItsClone() {
        assertEquals(cell, cell.clone());
    }

    @Test
    void equalsToItself() {
        assertEquals(cell, cell);
    }

    @Test
    void notEqualsToNull() {
        assertNotEquals(cell, null);
    }

    @Test
    void testHashCodeNull() {
        assertEquals(0, cell.hashCode());
    }

    @Test
    void testHashCode() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals(blackStone.hashCode(), cell.hashCode());
    }
    //endregion
}