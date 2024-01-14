import Vector.Vector;
import Vector.Line;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

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

        // make sure the line ends in the correct place
        assertFloatEquals(v2.x, line.endPoint().x);
        assertFloatEquals(v2.y, line.endPoint().y);
        
        Random rand = new Random();
        // repeat this test with 100 random lines
        for(int i = 0; i < 100; i++){
            v1 = new Vector(rand.nextInt(1001), rand.nextInt(1001));
            v2 = new Vector(rand.nextInt(1001), rand.nextInt(1001));
            line = new Line(v1, v2);

            // make sure the line is pointing in the right direction
            lineDirection = v2.sub(v1);
            float angleDiff = lineDirection.angleDiff(line.getDirection());
            assertFloatEquals(0, angleDiff, 0.001f);

            // make sure the line is the correct length
            assertFloatEquals(lineDirection.mag(), line.getLength());

            // make sure the line is starting in the correct place
            assertFloatEquals(v1.x, line.getPosition().x);
            assertFloatEquals(v1.y, line.getPosition().y);

            // make sure the line ends in the correct place
            assertFloatEquals(v2.x, line.endPoint().x);
            assertFloatEquals(v2.y, line.endPoint().y);
        }
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