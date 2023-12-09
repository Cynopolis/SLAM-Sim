import ScanGraph.ScanMatcher;
import ScanGraph.ScanPoint;
import Vector.Vector;
import processing.core.PApplet;

import java.util.ArrayList;

public class MatcherVisualizer extends PApplet{

    public static PApplet processing;
    ScanPoint referenceScan;
    ScanPoint scanToMatch;
    ScanPoint scanBeingMatched;

    public static void main(String[] args) {
        PApplet.main("MatcherVisualizer");
    }

    public void settings(){
        processing = this;
        size(1000, 1000);

        // generate two scans rotated by 45 degrees and append them together
        Vector descriptor = new Vector(200, 200);
        ScanPoint scan1 = generateScanPoint(new Vector(500, 500), descriptor, 12);
        ScanPoint scan2 = generateScanPoint(new Vector(500, 500), descriptor.rotate2D((float) Math.PI / 4), 12);
        this.referenceScan = appendScanPoints(scan1, scan2);

        // generate two scans offset by some amount and rotated by 55 degrees and append them together
        Vector rotated = descriptor.rotate2D((float) Math.PI);
        ScanPoint scan4 = generateScanPoint(new Vector(250, 300), rotated, 12);
        ScanPoint scan5 = generateScanPoint(new Vector(250, 300), rotated.rotate2D((float) Math.PI / 4), 12);
        this.scanToMatch = appendScanPoints(scan4, scan5);
        this.scanBeingMatched = new ScanPoint(this.scanToMatch);
    }
    public void draw(){
        iterativeScanMatch();
//        background(0);
    }

    /**
     * @brief Generate a scan point from a scan description
     * @param offset The offset of the scan point from the origin
     * @param scanDescription A vector which describes the length of the line and direction of the line
     * @return A scan point with the given offset and scan description
     */
    public static ScanPoint generateScanPoint(Vector offset, Vector scanDescription, int numPoints){
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
    public static ScanPoint appendScanPoints(ScanPoint scan1, ScanPoint scan2){
        ArrayList<Vector> points = new ArrayList<>();
        points.addAll(scan1.getPoints());
        points.addAll(scan2.getPoints());
        return new ScanPoint(new Vector(0, 0), 0, points);
    }

    public void delayMillis(long millis){
        // get the current time
        long start = System.currentTimeMillis();
        long end = start + millis;
        while(System.currentTimeMillis() < end){
            // do nothing
        }
    }

    /**
     * @brief Draw a scan point to the screen
     * @param scan The scan point to draw
     * @param color The color to draw the scan point
     */
    public void drawScan(ScanPoint scan, int[] color) {
        processing.stroke(color[0], color[1], color[2]);
        processing.fill(color[0], color[1], color[2]);
        ArrayList<Vector> points = scan.getPoints();
        for (int i = 0; i < points.size() - 1; i++) {
            Vector point = points.get(i);
            processing.ellipse(point.x, point.y, 5, 5);
        }
    }

    public void iterativeScanMatch() {
        background(0);
        int[] red = {255, 0, 0};
        int[] green = {0, 255, 0};
        int[] blue = {0, 0, 255};



        drawScan(this.referenceScan, red);
        delayMillis(10);
        drawScan(this.scanToMatch, green);

        // do a single scan match and calculate the error
        ScanMatcher matcher = new ScanMatcher();
        matcher.calculateRotationAndTranslationMatrices(this.referenceScan, this.scanBeingMatched);
        this.scanBeingMatched = matcher.applyRotationAndTranslationMatrices(this.scanBeingMatched);
        float singleScanMatchError = matcher.getError(this.referenceScan, this.scanBeingMatched);
        delayMillis(10);
        drawScan(this.scanBeingMatched, blue);

        // do an iterative scan match and calculate the error
//        ScanPoint matchedScan = matcher.iterativeScanMatch(scan1, scan2, 0.01f, 10);

//        float iterativeScanMatchError = matcher.getError(scan1, matchedScan);
        float x = 10+10;
        float y = x+10;
    }
}
