package cs361.battleships.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;

public class Ship {

	@JsonProperty protected List<Square> occupiedSquares;

	protected int hitNum;  // to record # of hits on this ship
	protected String kind;
	protected int shipSize;

	public Ship() { occupiedSquares = new ArrayList<Square>(); }

	public Ship(String kind) {
		setKind(kind);
		hitNum = 0;
		occupiedSquares = new ArrayList<Square>();
	}

	public boolean setOccupiedSquares(int x, int y, boolean isVertical){
		// Check if the ship goes over the edge of the board.
		if (checkForPlacementConflicts(x, y, isVertical, shipSize)) return false;

		addOccupiedSquares(x, y, isVertical, shipSize);

		return true;
	}

	private void addOccupiedSquares(int x, int y, boolean isVertical, int shipSize) {
		for(int i = 0; i < shipSize; i++) {
			if (isVertical) {
				occupiedSquares.add(new Square(x + i, (char)y));
			}
			else
				occupiedSquares.add(new Square( x, (char)(y + i)));
		}
	}

	private boolean checkForPlacementConflicts(int x, int y, boolean isVertical, int shipSize) {
		if(isVertical && x + shipSize - 1 > 10)
		{
			return true;
		}
		//if it is not vertical check if it goes over the right edge
		else if(!isVertical && y + shipSize - 1 > 'J')
		{
			return true;
		}
		return false;
	}

	public List<Square> getOccupiedSquares() {
		return occupiedSquares;
	}

	public void setKind(String kind){
		this.kind = kind;
	}

	public String getKind(){
		return kind;
	}

	// getter and setters for hitNum
	public void setHitNum(int num) {
		this.hitNum = num;
	}
	public int getHitNum() {
		return this.hitNum;
	}
}
