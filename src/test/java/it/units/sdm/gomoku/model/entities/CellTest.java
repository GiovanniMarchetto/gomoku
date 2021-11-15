package it.units.sdm.gomoku.model.entities;

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

class CellTest { // TODO: rethink this

    private static final Stone blackStone = new Stone(Stone.Color.BLACK);
    private static final Stone whiteStone = new Stone(Stone.Color.WHITE);
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
    void voidConstructorTest() {
        assertTrue(cell.isEmpty());
    }

    @Test
    void copyConstructorTest() {
        cell.setStoneFromColor(blackStone.getColor());
        Cell cell2 = new Cell(cell);
        assertEquals(cell, cell2);
    }

    @Test
    void getStoneEmpty() {
        assertNull(cell.getStone());
    }

    @Test
    void getStoneBlack() {
        try {
            TestUtility.setFieldValue("stone", blackStone, cell);
            assertEquals(blackStone, cell.getStone());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void setStone() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals(blackStone, cell.getStone());
    }

    @Test
    void isEmptyTrue() {
        assertTrue(cell.isEmpty());
    }

    @Test
    void isEmptyFalse() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertFalse(cell.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void isBelongingToChainOfTwoCellsInListWithChainOfTwo(int value) throws NoSuchFieldException, IllegalAccessException {
        NonNegativeInteger n = new NonNegativeInteger(value);
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals(value < 3,
                cell.isBelongingToChainOfNCellsInList(n, cellList));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
    void isBelongingToChainOfTwoCellsInListWithChainOfFive(int value) throws NoSuchFieldException, IllegalAccessException {
        NonNegativeInteger n = new NonNegativeInteger(value);
        TestUtility.setFieldValue("stone", whiteStone, cell);
        assertEquals(value < 6,
                cell.isBelongingToChainOfNCellsInList(n, cellList));
    }

    @Test
    void testClone() {
        assertEquals(cell, cell.clone());
    }

    @Test
    void testToStringEmpty() {
        assertEquals(" ", cell.toString());
    }

    @Test
    void testToStringBlack() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", blackStone, cell);
        assertEquals("X", cell.toString());
    }

    @Test
    void testToStringWhite() throws NoSuchFieldException, IllegalAccessException {
        TestUtility.setFieldValue("stone", whiteStone, cell);
        assertEquals("O", cell.toString());
    }

    @Test
    void testEqualsItself() {
        assertEquals(cell, cell);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(cell, null);
    }

    @Test
    void testEqualsEmptyCells() {
        Cell cell1 = new Cell();
        assertEquals(cell, cell1);
    }

    @Test
    void testEqualsBlackStone() throws NoSuchFieldException, IllegalAccessException {
        Cell cell1 = new Cell();
        TestUtility.setFieldValue("stone", blackStone, cell);
        TestUtility.setFieldValue("stone", blackStone, cell1);
        assertEquals(cell, cell1);
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
}