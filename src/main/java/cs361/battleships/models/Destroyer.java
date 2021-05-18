package cs361.battleships.models;

import java.util.ArrayList;

public class Destroyer extends Ship {

    public Destroyer() {
        setKind("DESTROYER");
        hitNum = 0;
        occupiedSquares = new ArrayList<Square>();
        shipSize = 3;
    }
}
