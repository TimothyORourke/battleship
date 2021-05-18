package cs361.battleships.models;

import org.junit.Test;

import static cs361.battleships.models.AtackStatus.HIT;
import static cs361.battleships.models.AtackStatus.INVALID;
import static cs361.battleships.models.AtackStatus.MISS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ResultTest {

    @Test
    public void testSetAndGetResult() {
        Result result = new Result();
        result.setResult(HIT);

        assertEquals(HIT, result.getResult());
    }

    @Test
    public void testSetAndGetShip() {
        Result result = new Result();
        Ship destroyer = new Ship("DESTROYER");
        result.setShip(destroyer);

        assertEquals(destroyer, result.getShip());
    }

    @Test
    public void testSetAndGetLocation() {
        Result result = new Result();
        Square location = new Square(3, 'A');
        result.setLocation(location);

        assertEquals(location, result.getLocation());
    }
}
