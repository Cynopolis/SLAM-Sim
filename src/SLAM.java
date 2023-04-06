import processing.core.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static processing.core.PApplet.*;

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
    private List<Vector> randomSample(ArrayList<Vector> set, int indexRange, int subSampleSize){
        // select a random laser data reading
        int randomIdx = (int) proc.random(set.size() - 1); // index of starter reading
        Vector point = set.get(randomIdx); // point of starter reading

        // get a random sample of size numSampleReadings within degreeRange degrees of this laser reading.
        List<Vector> subSample;
        int rangeStart = randomIdx - indexRange >= 0 ? randomIdx - indexRange : 0;
        int rangeEnd = randomIdx + indexRange < set.size() ? randomIdx + indexRange : set.size()-1;
        subSample = set.subList(rangeStart, rangeEnd); // get the sub-sample
        Collections.shuffle(subSample); // shuffle the list
        List<Vector> randomSample = subSample.subList(0, rangeEnd-rangeStart); // get our random sample
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
    private void extractFeature(ArrayList<Vector> originalList, List<Vector> randomSample, float maxRange, int consensus){
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
                originalList.remove(v);
            }
        }
    }

    /**
     * @param newPoints a new scan of points to perform feature detection on
     * @param raysPerDegree How many degrees apart are each ray that was cast
     */
    public void RANSAC(ArrayList<Vector> newPoints, float raysPerDegree){
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
            List<Vector> randomSample = this.randomSample(newPoints, indexRange, numSampleReadings);

            // check if the sub sample forms a valid line and remove the randomSample points if it does.
            extractFeature(newPoints, randomSample, maxRange, consensus);

        }

    }

    public void drawLines(){
        for(Line line : lines){
            line.draw(proc);
        }
    }

}