package ScanGraph;

import org.ejml.simple.SimpleMatrix;
import org.junit.jupiter.api.Test;
import Vector.Vector;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ScanMatcherTest {

    /**
     * @brief Generate a scan point from a scan description
     * @param offset The offset of the scan point from the origin
     * @param scanDescription A vector which describes the length of the line and direction of the line
     * @return A scan point with the given offset and scan description
     */
    ScanPoint generateScanPoint(Vector offset, Vector scanDescription, int numPoints){
        // generate a scan point with the given offset and scan description
        ArrayList<Vector> scan = new ArrayList<>();

        // divide the scan description by the number of points to allow us to scale it back up in the loop
        Vector directionVector = scanDescription.div(numPoints-1);

        for (int i = 0; i < numPoints; i++) {
            scan.add(offset.add(directionVector.mul(i)));
        }

        return new ScanPoint(new Vector(0, 0), 0, scan);
    }

    @Test
    void applyRotationAndTranslationMatrices() {
        // generate one scan that is level and another that is rotated 45 degrees.
        Vector scanDescription = new Vector(10, 0);
        ScanPoint referenceScan = generateScanPoint(new Vector(0, 0), scanDescription, 10);
        ScanPoint newScan = generateScanPoint(new Vector(0, 0), scanDescription.rotate2D((float) Math.PI / 4), 10);

        Vector test = scanDescription.rotate2D((float) Math.PI / 4);
        float mag = test.mag();

        // calculate the rotation and translation matrices between the two scans
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(referenceScan, newScan);
        // apply the rotation and translation matrices to the new scan
        ScanPoint newScanWithRotationAndTranslation = matcher.applyRotationAndTranslationMatrices(newScan);

        // Get the first and last points of the new scan with rotation and translation and calculate the angle between them
        ArrayList<Vector> points = newScanWithRotationAndTranslation.getPoints();
        Vector firstPoint = points.get(0);
        Vector lastPoint = points.get(points.size() - 1);
        Vector rotatedDirection = lastPoint.sub(firstPoint);
        float angle = scanDescription.angleDiff(rotatedDirection);

        // The angle between the first and last points should be zero
        assertEquals(0, angle);
    }

    @Test
    void getError() {
        // generate two scans that are the same. The error should be zero.
        ScanPoint scan1 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        ScanPoint scan2 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(scan1, scan2);
        assertEquals(0, matcher.getError(scan1, scan2));
    }

    @Test
    void iterativeScanMatch() {
        // TODO: Write a test for this
    }
}