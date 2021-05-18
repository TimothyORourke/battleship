package cs361.battleships.models;

public enum AtackStatus {

	/**
	 * The result if an attack results in a miss.
	 */
	MISS,

	/**
	 * The result if an attack results in a hit on an enemy ship.
	 */
	HIT,

	/**
	 * The result if an attack sinks the enemy ship
	 */
	SUNK,

	//the result to be added to previous hits when a ship is sunk
	HITTOSUNK,

	/**
	 * The results if an attack results in the defeat of the opponent (a
	 * surrender).
	 */
	SURRENDER,
	
	/**
	 * The result if the coordinates given are invalid.
	 */
	INVALID,

	/**
	 * The result if the coordinates have already been guessed before.
	 */
	DUPLICATE,

}