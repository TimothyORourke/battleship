package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang.math.RandomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static cs361.battleships.models.AtackStatus.*;

public class Game {

    @JsonProperty private Board playersBoard = new Board();
    @JsonProperty private Board opponentsBoard = new Board();
    private Random  random = new Random();

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
        boolean successful = playersBoard.placeShip(ship, x, y, isVertical);
        if (!successful)
            return false;

        //opponent's ship, with the type copied from the player's type
        Ship opponentShip = null;
        if (ship.getKind().equals("MINESWEEPER"))
            opponentShip = new Minesweeper();
        else if (ship.getKind().equals("DESTROYER"))
            opponentShip = new Destroyer();
        else if (ship.getKind().equals("BATTLESHIP"))
            opponentShip = new Battleship();

        boolean opponentPlacedSuccessfully;
        do {
            // AI places random ships, so it might try and place overlapping ships
            // let it try until it gets it right
            opponentPlacedSuccessfully = opponentsBoard.placeShip(opponentShip, randRow(), randCol(), randVertical());
        } while (!opponentPlacedSuccessfully);

        return true;
    }

    /*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
    public boolean attack(int x, char  y) {
        Result playerAttackResult = opponentsBoard.attack(x, y);
        if (playerAttackResult.getResult() == INVALID) {
            return false;
        } 

        Result opponentAttackResult;
        do {
            // AI does random attacks, so it might attack the same spot twice
            // let it try until it gets it right
            opponentAttackResult = playersBoard.attack(randRow(), randCol());
        } while(opponentAttackResult.getResult() == INVALID);

        return true;
    }

    private char randCol() {
        int num = random.nextInt(10);
        return (char)(num + 65);
    }

    private int randRow() {
        //add one to the nextInt value because the boards bounds are [1, 10] and nextInt's bounds are [0, 10)
        return random.nextInt(10) + 1;
    }

    private boolean randVertical() {
        return random.nextBoolean();
    }


}
