package cs361.battleships.models;

import java.util.ArrayList;

public class Battleship extends Ship {

    public Battleship() {
        setKind("BATTLESHIP");
        hitNum = 0;
        occupiedSquares = new ArrayList<Square>();
        shipSize = 4;
    }
}
