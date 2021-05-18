package cs361.battleships.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Board {

	private List<Ship> shipList;
	private List<Result> resultList;
	private int sunkShips; // records how many ship on this board is sunk

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Board() {
		shipList = new ArrayList<Ship>();
		resultList = new ArrayList<Result>();
		sunkShips = 0;
	}

	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public boolean placeShip(Ship ship, int x, char y, boolean isVertical) {
		if (ship.setOccupiedSquares(x, y, isVertical)){
			//validate that the newly occupied squares don't overlap old ones or duplicate old kinds
			if(checkOverlaps(ship) && checkShipDuplicates(ship))
			{
				shipList.add(ship);
				return true;
			}
		}

		//if it failed either test then return false
		return false;
	}

	



	/*
	DO NOT change the signature of this method. It is used by the grading scripts.
	 */
	public Result attack(int x, char y) {
		Result result = new Result();

		if (invalidParameters(x, y)) {
			setResultToInvalid(x, y, result);
			return result;
		}

		Iterator resultIterator = resultList.iterator();
		if (checkForDuplicate(x, y, result, resultIterator)) {
			return result;
		}

		if (checkEachShipForAHit(x, y, result)) {
			return result;
		}

		if (isMiss(result)) {
			setResultToMiss(x, y, result);
		}

		addToResultList(result, getAttacks());

		return result;
	}

	private boolean checkEachShipForAHit(int x, char y, Result result) {
		Iterator shipIterator = shipList.iterator();
		while (shipIterator.hasNext()) {
			Ship ship = (Ship) shipIterator.next();

			// Check to see if this ship occupies the coordinate.
			List<Square> occupiedSquares = ship.getOccupiedSquares();
			Iterator squaresIterator = occupiedSquares.iterator();
			while (squaresIterator.hasNext()) {
				Square square = (Square) squaresIterator.next();

				// If occupies the coordinate:
				//		hits the ship
				//		check how many times this ship is hit
				// 		check how many ship are hit on this board
				if (isSamePosition(x, y, square)) {
					setResultToHit(result, ship, square);

					increaseShipHitNumber(ship);

					if (checkForSunkOrSurrender(result, ship)) return true;
				}
			}
		}
		return false;
	}

	private boolean checkForSunkOrSurrender(Result result, Ship ship) {
		int shipSize = getShipSize(ship);

		if (areAllSquaresHit(ship, shipSize)) {
			List<Result> list = getAttacks();
			setPreviousHitsToSunk(ship, list);

			result.setResult(AtackStatus.SUNK);
			setSunkShips(getSunkShips() + 1);
			if (getSunkShips() == 3) {
				result.setResult(AtackStatus.SURRENDER);
			}

			addToResultList(result, list);
			return true;
		}
		return false;
	}

	private boolean areAllSquaresHit(Ship ship, int shipSize) {
		return ship.getHitNum() == shipSize;
	}

	private void setResultToHit(Result result, Ship ship, Square square) {
		result.setResult(AtackStatus.HIT);
		result.setLocation(square);
		result.setShip(ship);
	}

	private void setPreviousHitsToSunk(Ship ship, List<Result> list) {
		Iterator occupiedSquaresIterator = ship.getOccupiedSquares().iterator();
		while (occupiedSquaresIterator.hasNext()){
			Result newResult = new Result();
			Square prev_square = (Square) occupiedSquaresIterator.next();
			newResult.setResult(AtackStatus.HITTOSUNK);
			newResult.setLocation(prev_square);
			list.add(newResult);
		}
	}

	private int getShipSize(Ship ship) {
		String kind = ship.getKind();
		int numberOfSquares = 0;
		if (kind.equals("MINESWEEPER")){
			numberOfSquares = 2;
		}
		else if (kind.equals("DESTROYER")){
			numberOfSquares = 3;
		}
		else if (kind.equals("BATTLESHIP")){
			numberOfSquares = 4;
		}
		return numberOfSquares;
	}

	private void setResultToMiss(int x, char y, Result result) {
		result.setResult(AtackStatus.MISS);
		result.setLocation(new Square(x, y));
	}

	private boolean isMiss(Result result) {
		return result.getResult() != AtackStatus.HIT && result.getResult() != AtackStatus.SUNK && result.getResult() != AtackStatus.SURRENDER;
	}

	private boolean isSamePosition(int x, char y, Square square) {
		return square.getRow() == x && square.getColumn() == y;
	}

	private void increaseShipHitNumber(Ship ship) {
		int numHit = ship.getHitNum() + 1;
		ship.setHitNum(numHit);
	}

	private boolean checkForDuplicate(int x, char y, Result result, Iterator resultIterator) {
		while (resultIterator.hasNext()) {
			Result resultListElement = (Result) resultIterator.next();

			if (isSamePosition(x, y, resultListElement.getLocation())) {
				setResultToDuplicate(x, y, result);
				return true;
			}
		}
		return false;
	}

	private void setResultToDuplicate(int x, char y, Result result) {
		result.setResult(AtackStatus.DUPLICATE);
		result.setLocation(new Square(x, y));
		List<Result> list = getAttacks();
		addToResultList(result, list);
	}

	private void setResultToInvalid(int x, char y, Result result) {
		result.setResult(AtackStatus.INVALID);
		result.setLocation(new Square(x, y));
		List<Result> list = getAttacks();
		addToResultList(result, list);
	}

	private boolean invalidParameters(int x, char y) {
		return x < 1 || x > 10 || y < 'A' || y > 'J';
	}

	private void addToResultList(Result result, List<Result> list) {
		list.add(result);
		setAttacks(list);
	}


	public int getSunkShips() {
		return sunkShips;
	}

	public void setSunkShips(int num) {
		this.sunkShips = num;
	}

	public List<Ship> getShips() {
		return shipList;
	}

	public void setShips(List<Ship> ships) {
		this.shipList = ships;
	}

	public List<Result> getAttacks() {
		return resultList;
	}

	public void setAttacks(List<Result> attacks) {
		this.resultList = attacks;
	}

	//function to check for overlapping ship placement
	private boolean checkOverlaps(Ship newShip)
	{
		//go through each of the new ship's occupied squares and look for overlaps in the board's already placed ships
		List<Square> newSquares = newShip.getOccupiedSquares();
		for(int i = 0; i < newSquares.size(); i++)
		{
			//grab one of the new ship's occupied squares
			Square newSquare = newSquares.get(i);

			//loop through the list of ships already in place on this board
			for(int j = 0; j < shipList.size(); j++ )
			{
				//grab one of the existing ship's occupied squares
				List<Square> oldSquares = shipList.get(j).getOccupiedSquares();

				//loop through the old occupied squares
				for(int k = 0; k < oldSquares.size(); k++)
				{
					//if the new and old occupied squares have the same coordinates then return false for the validation
					if(newSquare.getColumn() == oldSquares.get(k).getColumn() && newSquare.getRow() == oldSquares.get(k).getRow())
					{
						return false;
					}
				}
			}
		}

		//if it gets through the whole list without returning then this is a non occupied square
		return true;
	}

	//function to check if a ship of the selected kind already exists. Returns false if one already exists.
	private boolean checkShipDuplicates(Ship newShip)
	{
		//the requested ship's kind
		String newKind = newShip.getKind();

		//loop through the existing ships to check against the new one
		for(int i = 0; i < shipList.size(); i++)
		{
			//check the existing ship against the new one
			if(newKind.equals(shipList.get(i).getKind()))
			{
				//return false if they are the same kind
				return false;
			}
		}
		//return true if it gets through the loop without encountering a duplicate.
		return true;
	}
}