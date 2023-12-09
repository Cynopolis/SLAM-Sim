import ScanGraph.ScanMatcher;
import ScanGraph.ScanPoint;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import processing.core.PApplet;

import org.junit.jupiter.api.Test;
import Vector.Vector;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static processing.core.PApplet.main;

class ScanMatcherTest{
    /**
     * @brief Generate a scan point from a scan description
     * @param offset The offset of the scan point from the origin
     * @param scanDescription A vector which describes the length of the line and direction of the line
     * @return A scan point with the given offset and scan description
     */
    public ScanPoint generateScanPoint(Vector offset, Vector scanDescription, int numPoints){
        // generate a scan point with the given offset and scan description
        ArrayList<Vector> scan = new ArrayList<>();

        // divide the scan description by the number of points to allow us to scale it back up in the loop
        Vector directionVector = scanDescription.div(numPoints-1);

        for (int i = 0; i < numPoints; i++) {
            scan.add(offset.add(directionVector.mul(i)));
        }

        return new ScanPoint(new Vector(0, 0), 0, scan);
    }

    /**
     * @brief Append two scan points together
     * @param scan1 The first scan point to append
     * @param scan2 The second scan point to append
     * @return A scan point that is the combination of the two scan points
     */
    public ScanPoint appendScanPoints(ScanPoint scan1, ScanPoint scan2){
        ArrayList<Vector> points = new ArrayList<>();
        points.addAll(scan1.getPoints());
        points.addAll(scan2.getPoints());
        return new ScanPoint(new Vector(0, 0), 0, points);
    }

    @Test
    public void applyRotationAndTranslationMatrices() {
        // generate one scan that is level and another that is rotated 45 degrees.
        Vector scanDescription = new Vector(10, 0);
        ScanPoint referenceScan = generateScanPoint(new Vector(0, 0), scanDescription, 10);
        ScanPoint newScan = generateScanPoint(new Vector(0, 0), scanDescription.rotate2D((float) Math.PI / 4), 10);

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
    public void getError() {
        // generate two scans that are the same. The error should be zero.
        ScanPoint scan1 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        ScanPoint scan2 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(scan1, scan2);
        assertEquals(0, matcher.getError(scan1, scan2));

        // generate two scans that are the same but one is offset by 10 in the y direction. The error should be 10.
        scan1 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        scan2 = generateScanPoint(new Vector(0, 10), new Vector(10, 10), 12);
        matcher.calculateRotationAndTranslationMatrices(scan1, scan2);
        assertEquals(10, matcher.getError(scan1, scan2));

        // generate two scans that are the same but one is rotated by 45 degrees. The error should be near zero.
        scan1 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        scan2 = generateScanPoint(new Vector(0, 0), new Vector(10, 10).rotate2D((float) Math.PI / 4), 12);
        matcher.calculateRotationAndTranslationMatrices(scan1, scan2);
        assertEquals(0, matcher.getError(scan1, scan2), 0.1);
    }

    @Test
    public void iterativeScanMatch() {
        float bendAngle = (float) (5 * Math.PI / 9);
        // generate two scans rotated by 45 degrees and append them together
        ScanPoint scan1 = generateScanPoint(new Vector(0, 0), new Vector(10, 10), 12);
        ScanPoint scan2 = generateScanPoint(new Vector(0, 0), new Vector(10, 10).rotate2D(bendAngle), 12);
        ScanPoint scan3 = appendScanPoints(scan1, scan2);


        // generate two scans offset by some amount and rotated by 55 degrees and append them together
        Vector rotated = (new Vector(10, 10)).rotate2D((float) Math.PI);
        ScanPoint scan4 = generateScanPoint(new Vector(10, 10), rotated, 12);
        ScanPoint scan5 = generateScanPoint(new Vector(10, 10), rotated.rotate2D(bendAngle), 12);
        ScanPoint scan6 = appendScanPoints(scan4, scan5);


        // do a single scan match and calculate the error
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(scan3, scan6);
        ScanPoint oneCalcMatch = matcher.applyRotationAndTranslationMatrices(scan6);
        float singleScanMatchError = matcher.getError(scan3, oneCalcMatch);


        // do an iterative scan match and calculate the error
        ScanPoint matchedScan = matcher.iterativeScanMatch(scan1, scan2, 0.0001f, 10);

        // if it's null something has gone wrong with the algorithm because these scans can easily be matched.
        assertNotNull(matchedScan);

        float iterativeScanMatchError = matcher.getError(scan1, matchedScan);

        // the iterative scan match should have a lower error than the single scan match
        assertTrue(iterativeScanMatchError < singleScanMatchError);
    }
}