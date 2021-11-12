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

class CellTest {

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
    static void setCellList() {
        blackCell.setStone(blackStone);
        whiteCell.setStone(whiteStone);
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
        cell.setStone(blackStone);
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
    void setStone() {
        cell.setStone(blackStone);
        assertEquals(blackStone, cell.getStone());
    }

    @Test
    void isEmptyTrue() {
        assertTrue(cell.isEmpty());
    }

    @Test
    void isEmptyFalse() {
        cell.setStone(blackStone);
        assertFalse(cell.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void isBelongingToChainOfTwoCellsInListWithChainOfTwo(int value) {
        NonNegativeInteger n = new NonNegativeInteger(value);
        cell.setStone(blackStone);
        assertEquals(value < 3,
                cell.isBelongingToChainOfNCellsInList(n, cellList));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5, 6, 7, 8})
    void isBelongingToChainOfTwoCellsInListWithChainOfFive(int value) {
        NonNegativeInteger n = new NonNegativeInteger(value);
        cell.setStone(whiteStone);
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
    void testToStringBlack() {
        cell.setStone(blackStone);
        assertEquals("X", cell.toString());
    }

    @Test
    void testToStringWhite() {
        cell.setStone(whiteStone);
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
    void testEqualsBlackStone() {
        Cell cell1 = new Cell();
        cell.setStone(blackStone);
        cell1.setStone(blackStone);
        assertEquals(cell, cell1);
    }

    @Test
    void testHashCodeNull() {
        assertEquals(0, cell.hashCode());
    }

    @Test
    void testHashCode() {
        cell.setStone(blackStone);
        assertEquals(blackStone.hashCode(), cell.hashCode());
    }
}