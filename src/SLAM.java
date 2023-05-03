import Vector.*;
import processing.core.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Math.random;
import static processing.core.PApplet.*;

public class SLAM{
    ArrayList<Line> lines = new ArrayList<>();
    ArrayList<Vector> unassociatedPoints = new ArrayList<>();
    private static PApplet proc;

    SLAM(PApplet processing){
        proc = processing;
    }

    /**
     * @param set the set to take a sub sample of
     * @param subSampleSize the size of the sub sample
     * @param minAngle the minimum angle allowed in the subset
     * @param maxAngle the maximum angle allowed in the subset
     * @return A random subset of the set within the angle range
     */
    private List<Vector> randomSampleInAngleRange(ArrayList<Vector> set, int subSampleSize, float minAngle, float maxAngle){

        // create an arraylist with all points within the angle range fro mthe given set
        ArrayList<Vector> pointsInAngleRange = new ArrayList<>();
        for(Vector point : set){
            if(minAngle <= point.z && point.z <= maxAngle){
                pointsInAngleRange.add(point);
            }
        }

        // shuffle the list to randomize it
        Collections.shuffle(pointsInAngleRange);

        // if the list is too small, just return the whole list
        if(pointsInAngleRange.size() < subSampleSize){
            return pointsInAngleRange;
        }
        // return a subSample of the list
        return pointsInAngleRange.subList(0, subSampleSize);
    }

    /**
     * @param randomSample a random subsampling of points from the originalList
     * @param maxRange the maximum distance away from the line of best fit of the subSample of points for a given point's consensus to count.
     * @param consensus the number of points that have to give their consensus for the line of best fit to count as a valid feature.
     */
    private void extractFeature(List<Vector> randomSample, float maxRange, int consensus){
        // get a line of best fit for this list.
        Line bestFit = new Line(randomSample);
        int count = 0;
        ArrayList<Vector> newRandomSample = new ArrayList<>();
        for (Vector v : randomSample) {
            if (bestFit.getDistance(v) <= maxRange) {
                count++;
                newRandomSample.add(v);
            }
        }
        // if the count is above the consensus, add the line to our list and remove the points that gave the consensus.
        if (count >= consensus) {
            bestFit = new Line(newRandomSample.subList(0, newRandomSample.size() - 1));
            lines.add(bestFit);
            // remove the associated readings from the total available readings.
            for (Vector v : newRandomSample) {
                this.unassociatedPoints.remove(v);
            }
        }
    }

    /**
     * @param view a laser scan view
     */
    public void RANSAC(View view){
        unassociatedPoints.addAll(view.getPoints());

        float degreeRange = radians(25/2); // range to randomly sample readings within
        int numSampleReadings = 10; // number of readings to randomly sample

        int consensus = 7; // the number of points that need to lie near a line for it to be considered valid.
        float maxRange = 5; // the maximum distance a point can be away from the line for it to count as a consensus

        // this for loop determines the maximum number of trials we're willing to do.
        for(int j = 0; j < 20; j++) {
            // if there aren't enough points left in the set to form a consensus, we're done.
            if(this.unassociatedPoints.size() < maxRange){
                break;
            }

            // get a random angle between -PI and PI
            float randomAngle = (float) (2*PI*(random()) - 0.5);

            // get a random sub sample of newPoints within the index range of a given size
            List<Vector> randomSample = this.randomSampleInAngleRange(this.unassociatedPoints, numSampleReadings, randomAngle-degreeRange, randomAngle+degreeRange);

            // check if the sub sample forms a valid line and remove the randomSample points if it does.
            extractFeature(randomSample, maxRange, consensus);

        }

    }

    public void drawFeatures(PApplet proc){
        for(Line line : lines){
            line.draw(proc);
        }
    }

}