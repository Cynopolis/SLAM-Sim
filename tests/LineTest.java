import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

class LineTest{
    public void assertFloatEquals(float expected, float actual){
        assertFloatEquals(expected, actual, (float)0.0001);
    }

    public void assertFloatEquals(float expected, float actual, float range){
        assertTrue(abs(expected-actual) < range);
    }
    @Test
    public void P2PLine(){
        Vector v1 = new Vector(10, 0);
        Vector v2 = new Vector(10, 10);
        Line line = new Line(v1, v2);

        // make sure the line is pointing in the right direction
        Vector lineDirection = v2.sub(v1);
        assertFloatEquals(lineDirection.angleDiff(line.getDirection()), 0);

        // make sure the line is the correct length
        assertFloatEquals(lineDirection.mag(), line.getLength());

        // make sure the line is starting in the correct place
        assertFloatEquals(v1.x, line.getPosition().x);
        assertFloatEquals(v1.y, line.getPosition().y);
    }

    @Test
    public void bestFitLine(){
        Vector v1 = new Vector(10, 0);
        Vector v2 = new Vector(10, 10);
        ArrayList<Vector> points = new ArrayList<>();
        points.add(v1);
        points.add(v2);
        Line line = new Line(points);

        // make sure the line is pointing in the right direction
        Vector lineDirection = v2.sub(v1);
        assertFloatEquals(lineDirection.angleDiff(line.getDirection()), 0);

        // make sure the line is the correct length
        assertFloatEquals(lineDirection.mag(), line.getLength());

        // make sure the line is starting in the correct place
        assertFloatEquals(v1.x, line.getPosition().x);
        assertFloatEquals(v1.y, line.getPosition().y);
    }

}