import org.junit.jupiter.api.Test;
import processing.core.PApplet;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

class VectorTest{

    @Test
    public void vector2DOperations(){
        for(int i = 0; i < 20; i++){
            float x1 = (float)(1000*random() - 500);
            float y1 = (float)(1000*random() - 500);
            Vector v1 = new Vector(x1, y1);
            for(int j = 0; j < 20; j++){
                float x2 = (float)(1000*random());
                float y2 = (float)(1000*random());
                Vector v2 = new Vector(x2, y2);

                // test general setters
                assertFloatEquals(x1, v1.x);
                assertFloatEquals(y1, v1.y);
                assertFloatEquals(x2, v2.x);
                assertFloatEquals(y2, v2.y);

                // test magnitude
                assertFloatEquals((float)sqrt(x1*x1 + y1*y1), v1.mag());
                assertFloatEquals((float)sqrt(x2*x2 + y2*y2), v2.mag());

                // test dot product
                assertFloatEquals((float)(x1*x2+y1*y2), v1.dot(v2));
                assertFloatEquals((float)(x1*x2+y1*y2), v2.dot(v1));

                // test addition
                Vector vSum = v1.add(v2);
                assertFloatEquals(x1+x2, vSum.x);
                assertFloatEquals(y1+y2, vSum.y);

                // test subtraction
                Vector vSub = v1.sub(v2);
                assertFloatEquals(x1-x2, vSub.x);
                assertFloatEquals(y1-y2, vSub.y);
                vSub = v2.sub(v1);
                assertFloatEquals(x2-x1, vSub.x);
                assertFloatEquals(y2-y1, vSub.y);

                // test scaling
                Vector vScale = v1.mul(x2);
                assertFloatEquals(x1*x2, vScale.x);
                assertFloatEquals(y1*x2, vScale.y);

                // test normalization
                Vector vNorm = v1.normalize();
                assertFloatEquals(1, vNorm.mag());
            }
        }
    }

    public void assertFloatEquals(float expected, float actual){
        assertFloatEquals(expected, actual, (float)0.0001);
    }

    public void assertFloatEquals(float expected, float actual, float range){
        assertTrue(abs(expected-actual) < range);
    }

    @Test
    public void testCrossProduct(){
        Vector v1 = new  Vector(1, 2, 3);
        Vector v2 = new Vector(4, 5, 6);
        Vector cross = v1.cross(v2);
        assertFloatEquals(-3, cross.x);
        assertFloatEquals(6, cross.y);
        assertFloatEquals(-3, cross.z);

        v1 = new Vector(-3, 7, -9);
        v2 = new Vector((float)2.6, 66, (float)-3.14159);
        cross = v1.cross(v2);
        assertFloatEquals((float)572.00887, cross.x, (float)0.1);
        assertFloatEquals((float)-32.82477, cross.y, (float)0.1);
        assertFloatEquals((float)-216.2, cross.z, (float)0.1);
    }

}