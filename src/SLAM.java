import static processing.core.PApplet.radians;
import processing.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static processing.core.PApplet.pow;

public class SLAM{
    ArrayList<Line> lines = new ArrayList<>();
    private static PApplet proc;

    SLAM(PApplet processing){
        proc = processing;
    }

    /**
     * @param set the set to take a sub sample of
     * @param indexRange the range within to take the sub sample
     * @param subSampleSize the size of the sub sample
     * @return A random subset of the set within an indexRange and of size: subSampleSize
     */
    private List<PVector> randomSample(ArrayList<PVector> set, int indexRange, int subSampleSize){
        // select a random laser data reading
        int randomIdx = (int) proc.random(set.size() - 1); // index of starter reading
        PVector point = set.get(randomIdx); // point of starter reading

        // get a random sample of size numSampleReadings within degreeRange degrees of this laser reading.
        List<PVector> subSample = set.subList(randomIdx - indexRange, randomIdx + indexRange); // get the sub-sample
        Collections.shuffle(subSample); // shuffle the list
        List<PVector> randomSample = subSample.subList(0, subSampleSize); // get our random sample
        if (!randomSample.contains(point)) {
            randomSample.add(point);
        }

        return randomSample;
    }

    /**
     * @param originalList the list which the randomSample of points originated from
     * @param randomSample a random subsampling of points from the originalList
     * @param maxRange the maximum distance away from the line of best fit of the subSample of points for a given point's consensus to count.
     * @param consensus the number of points that have to give their consensus for the line of best fit to count as a valid feature.
     */
    private void extractFeature(ArrayList<PVector> originalList, List<PVector> randomSample, float maxRange, int consensus){
        // get a line of best fit for this list.
        Line bestFit = new Line(proc, randomSample);
        int count = 0;
        ArrayList<PVector> newRandomSample = new ArrayList<>();
        for (PVector v : randomSample) {
            if (bestFit.getDistance(v) <= maxRange) {
                count++;
                newRandomSample.add(v);
            }
        }
        // if the count is above the consensus, add the line to our list and remove the points that gave the consensus.
        if (count >= consensus) {
            bestFit = new Line(proc, newRandomSample.subList(0, newRandomSample.size() - 1));
            lines.add(bestFit);
            // remove the associated readings from the total available readings.
            for (PVector v : newRandomSample) {
                originalList.remove(v);
            }
        }
    }
    public void RANSAC(ArrayList<PVector> newPoints, float raysPerDegree){
        float degreeRange = radians(10/2); // range to randomly sample readings within
        int indexRange = (int) (degreeRange / raysPerDegree);
        int numSampleReadings = 10; // number of readings to randomly sample
        // constrain numSampleReadings so that it cant be higher than possible
        if(numSampleReadings >= 2 * indexRange){
            numSampleReadings = 2 * indexRange;
        }
        int consensus = 6; // the number of points that need to lie near a line for it to be considered valid.
        float maxRange = 10; // the maximum distance a point can be away from the line for it to count as a consensus

        // this for loop determines the maximum number of trials we're willing to do.
        for(int j = 0; j < 20; j++) {
            // if there aren't enough points left in the set to form a consensus, we're done.
            if(newPoints.size() < consensus){
                break;
            }

            // get a random sub sample of newPoints within the index range of a given size
            List<PVector> randomSample = this.randomSample(newPoints, indexRange, numSampleReadings);

            // check if the sub sample forms a valid line and remove the randomSample points if it does.
            extractFeature(newPoints, randomSample, maxRange, consensus);

        }

    }

}

class Line{
    PVector direction = new PVector(0,0);
    PVector position = new PVector(0,0);
    private static PApplet proc;

    Line(PApplet processing){
        proc = processing;
    }
    Line(PApplet processing, PVector direction, PVector position){
        this.direction = direction;
        this.position = position;
        proc = processing;
    }

    /**
     * attempt to find the line of best fit for the given points
     * @param points the points to get the line of best for
     */
    Line(PApplet processing, List<PVector> points){
        bestFit(points);
        proc = processing;
    }

    // least squares line of best fit algorithm
    private void bestFit(List<PVector> points){
        // get the mean of all the points
        PVector mean = new PVector();
        for(PVector point : points){
            mean.add(point);
        }
        mean.div(points.size());

        // this section calculates the direction vector of the line of best fit
        PVector direction = new PVector();
        // get the rise and run of the line of best fit
        for(PVector point : points){
            direction.y += (point.x - mean.x)*(point.y - mean.y); // rise
            direction.x += pow((point.x - mean.x),2);
        }

        this.position = mean;
        this.direction = direction;
    }

    public PVector getSlopeIntForm(){
        float slope = direction.y / direction.x;
        float intercept = position.y - slope * position.x;
        return new PVector(slope, intercept);
    }

    public PVector getDirection(){
        return direction;
    }

    public PVector getPosition(){
        return position;
    }

    /**
     * @param point
     * @return the smallest distance from the point to this line
     */
    public float getDistance(PVector point){
        return (point.sub(position).cross(direction)).mag() / direction.mag();
    }
}