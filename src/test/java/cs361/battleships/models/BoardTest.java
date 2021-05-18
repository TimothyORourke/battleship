package cs361.battleships.models;

import org.junit.Test;

import static cs361.battleships.models.AtackStatus.*;
import static org.junit.Assert.*;

import java.util.List;

public class BoardTest {

    @Test
    public void testShipPlaced() {
        Board board = new Board();
        board.placeShip(new Minesweeper(), 5, 'C', true);
        List<Ship> shipList = board.getShips();
        assertFalse(((List) shipList).isEmpty());
    }

    @Test
    public void testThreeShips() {
        Board board = new Board();
        board.placeShip(new Minesweeper(), 5, 'C', true);
        board.placeShip(new Destroyer(), 5, 'D', true);
        board.placeShip(new Battleship(), 5, 'E', true);
        List<Ship> shipList = board.getShips();
        assertEquals(3, ((List) shipList).size());
    }

    @Test
    public void testInvalidPlacement() {
        Board board = new Board();
        assertFalse(board.placeShip(new Minesweeper(), 10, 'C', true));
        assertFalse(board.placeShip(new Minesweeper(), 5, 'J', false));
    }

    @Test
    public void testValidPlacement()
    {
        Board board = new Board();
        assertTrue(board.placeShip(new Minesweeper(), 9, 'C', true));
        assertTrue(board.placeShip(new Destroyer(), 5, 'H', false));
    }

    @Test
    public void testOverlappingPlacement()
    {
        Board board = new Board();
        //this ship takes up the following coordinates: (4, C), (4, D), (4, E), (4, F)
        board.placeShip(new Battleship(), 4, 'C', false);
        assertFalse(board.placeShip(new Destroyer(), 4, 'C', false));
        assertFalse(board.placeShip(new Destroyer(), 4, 'C', true));
        assertFalse(board.placeShip(new Destroyer(), 4, 'D', true));
        assertFalse(board.placeShip(new Destroyer(), 4, 'E', true));
        assertFalse(board.placeShip(new Destroyer(), 4, 'F', true));
        assertFalse(board.placeShip(new Destroyer(), 2, 'C', true));
        assertFalse(board.placeShip(new Destroyer(), 2, 'D', true));
        assertFalse(board.placeShip(new Destroyer(), 2, 'E', true));
        assertFalse(board.placeShip(new Destroyer(), 2, 'F', true));
    }

    @Test
    public void testRepeatShipKind()
    {
        Board board = new Board();
        board.placeShip(new Battleship(), 4, 'C', false);
        assertFalse(board.placeShip(new Battleship(), 5, 'C', false));
        board.placeShip(new Minesweeper(), 3, 'C', false);
        assertFalse(board.placeShip(new Minesweeper(), 5, 'C', false));
        board.placeShip(new Destroyer(), 2, 'C', false);
        assertFalse(board.placeShip(new Destroyer(), 5, 'C', false));
    }

    @Test
    public void testInvalidAttackParameters() {
        Board board = new Board();
        Result result1 = board.attack(11, 'K');
        Result result2 = board.attack(0, 'z');

        assertEquals(INVALID, result1.getResult());
        assertEquals(INVALID, result2.getResult());
    }

    @Test
    public void testAttackMiss() {
        Board board = new Board();
        board.placeShip(new Destroyer(), 2, 'B', false);
        Result result = board.attack(5, 'C');

        assertEquals(MISS, result.getResult());
    }

    @Test
    public void testAttackHit() {
        Board board = new Board();
        board.placeShip((new Minesweeper()), 4, 'C', true);
        Result result = board.attack(5, 'C');

        assertEquals(HIT, result.getResult());
    }

    @Test
    public void testAttackSameLocation() {
        Board board = new Board();
        board.placeShip((new Destroyer()), 3, 'A', false);
        board.attack(3, 'B');
        board.attack(4, 'J');
        Result result1 = board.attack(3, 'B');
        Result result2 = board.attack(4, 'J');

        assertEquals(DUPLICATE, result1.getResult());
        assertEquals(DUPLICATE, result2.getResult());
    }

    @Test
    public void testAttackSunk() {
        Board board = new Board();
        board.placeShip((new Minesweeper()), 5, 'A', false);
        board.attack(5, 'A');
        Result res = board.attack(5, 'B');
        assertEquals(SUNK, res.getResult());
    }

    @Test
    public void testAttackSurrender() {
        Board board = new Board();
        board.placeShip((new Battleship()), 3, 'A', true);
        board.placeShip((new Destroyer()), 3, 'B', true);
        board.placeShip((new Minesweeper()), 3, 'C', true);
        board.attack(3, 'A');
        board.attack(4, 'A');
        board.attack(5, 'A');
        board.attack(6, 'A');
        board.attack(3, 'B');
        board.attack(4, 'B');
        board.attack(5, 'B');
        board.attack(3, 'C');
        Result res = board.attack(4, 'C');
        assertEquals(SURRENDER, res.getResult());
    }
}
