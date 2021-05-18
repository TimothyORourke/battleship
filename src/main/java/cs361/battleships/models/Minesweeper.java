package cs361.battleships.models;

import java.util.ArrayList;

public class Minesweeper extends Ship{

    public Minesweeper() {
        setKind("MINESWEEPER");
        hitNum = 0;
        occupiedSquares = new ArrayList<Square>();
        shipSize = 2;
    }
}
