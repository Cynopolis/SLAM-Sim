package ScanGraph;

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
    ScanPoint generateScanPoint(Vector offset, Vector scanDescription){
        // generate a scan point with the given offset and scan description
        Vector scanPosition = new Vector(0, 0);
        ArrayList<Vector> scan = new ArrayList<>();

        // calculate the total number of points in the scan
        int numPoints = (int) scanDescription.mag();
        // calculate the slope of the line the scan is on
        float m = scanDescription.y / scanDescription.x;

        // add the points to the scan
        for(int i = 0; i < numPoints; i++){
            float x = i;
            float y = m * x;
            scan.add(new Vector(x + offset.x, y + offset.y));
        }

        return new ScanPoint(scanPosition, 0, scan);
    }

    @Test
    void applyRotationAndTranslationMatrices() {
        // generate one scan that is level and another that is rotated 45 degrees.
        Vector scanDescription = new Vector(10, 0);
        ScanPoint referenceScan = generateScanPoint(new Vector(0, 0), scanDescription);
        ScanPoint newScan = generateScanPoint(new Vector(0, 0), scanDescription.rotate2D((float) Math.PI / 4));

        // calculate the rotation and translation matrices between the two scans
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(referenceScan, newScan);
        // apply the rotation and translation matrices to the new scan
        ScanPoint newScanWithRotationAndTranslation = matcher.applyRotationAndTranslationMatrices(newScan);

        // Get the first and last points of the new scan with rotation and translation and calculate the angle between them
        ArrayList<Vector> points = newScanWithRotationAndTranslation.getPoints();
        Vector firstPoint = points.get(0);
        Vector lastPoint = points.get(points.size() - 1);
        float angle = firstPoint.angleDiff(lastPoint);

        // The angle between the first and last points should be zero
        assertEquals(0, angle);
    }

    @Test
    void getError() {
        // generate two scans that are the same. The error should be zero.
        ScanPoint scan1 = generateScanPoint(new Vector(0, 0), new Vector(10, 10));
        ScanPoint scan2 = generateScanPoint(new Vector(0, 0), new Vector(10, 10));
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(scan1, scan2);
        assertEquals(0, matcher.getError(scan1, scan2));
    }
}