package ScanGraph;

import Graph.Graph;
import Graph.Vertex;
import Vector.Vector;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import java.util.ArrayList;
public class ScanGraph extends Graph{

    ScanPoint lastPoint;
    public ScanGraph(ScanPoint startingPoint){
        super();
        this.lastPoint = startingPoint;
    }

    public void addEdge(ScanPoint vEnd){
        addVertex(vEnd);
        ScanEdge edge = new ScanEdge(this.lastPoint, vEnd);
        adjList.get((Vertex) this.lastPoint).add(edge);

        this.lastPoint = vEnd;
    }

    /**
     * @brief Get a new scan in and try to match it with all other scans in the graph
     * @param newScan the scan to match
     * @return null if no match can be found, or an existing scan the matches the new scan.
     */
    private ScanPoint getAssociatedScan(ScanPoint newScan) {
        ScanMatcher matcher = new ScanMatcher();
        // go through all of our available scans and try to match the new scan with the old scans. If no match can be found return null
        for (Vertex v : adjList.keySet()) {
            ScanPoint referenceScan = (ScanPoint) v;
            for(int i = 0; i < 5; i++) {
                // calculate the rotation and translation matrices between the new scan and the reference scan
                matcher.calculateRotationAndTranslationMatrices(referenceScan, newScan);

                // update the new scan with the rotation matrix and translation vector
                newScan = matcher.applyRotationAndTranslationMatrices(newScan);

                // calculate the error between the new scan and the reference scan
                float error = matcher.getError(referenceScan, newScan);

                // if the error is less than some threshold, then we have found a match
                if (error < 0.1) {
                    return referenceScan;
                }
            }
        }
        return null;
    }

}


/**
 * @brief A class to hold the correspondence matrix between two scans
 * The correspondence matrix is a 3xN matrix where N is the number of valid points in the scan.
 * This calculates the closest point in the old scan for each point in the new scan and gets rid of redundant closest points.
 */
class CorrespondenceMatrix{
    private ArrayList<Integer> oldPointIndices = new ArrayList<>();
    private ArrayList<Integer> newPointIndices = new ArrayList<>();
    private ArrayList<Float> distances = new ArrayList<>();

    CorrespondenceMatrix(ScanPoint newScan, ScanPoint oldScan){
        this.calculateCorrespondenceMatrix(newScan, oldScan);
    }

    public ArrayList<Integer> getOldPointIndices(){
        return this.oldPointIndices;
    }

    public ArrayList<Integer> getNewPointIndices(){
        return this.newPointIndices;
    }

    public ArrayList<Float> getDistances(){
        return this.distances;
    }

    /**
     * @brief Calculate the correspondence matrix between two scans
     * @param newScan the new scan
     * @param referenceScan the reference scan
     */
    private void calculateCorrespondenceMatrix(ScanPoint newScan, ScanPoint referenceScan){
        // compute the correspondence matrix between the two scans. It is a 3xN matrix where N is the number of points in the scan
        // Row 1 is the index of the point in the old scan
        // Row 2 is the index of the point in the new scan
        // Row 3 is the distance between the two points
        // if either scan has a null point, then skip that point

        // initialize the correspondence matrix as an array of array lists
        ArrayList<ArrayList<Float>> correspondenceMatrix = new ArrayList<ArrayList<Float>>();
        correspondenceMatrix.add(new ArrayList<Float>());
        correspondenceMatrix.add(new ArrayList<Float>());
        correspondenceMatrix.add(new ArrayList<Float>());

        // go through all of the points in the new scan and find the closest point in the old scan
        for (int newPointIndex = 0; newPointIndex < newScan.getScan().size(); newPointIndex++) {
            Vector newPoint = newScan.getScan().get(newPointIndex);
            // if the new point is null, then skip it
            if (newPoint == null) {
                continue;
            }
            // find the closest point in the old scan
            float closestDistance = Float.MAX_VALUE;
            int closestIndex = -1;
            for (int j = 0; j < referenceScan.getScan().size(); j++) {
                Vector oldPoint = referenceScan.getScan().get(j);
                // if the old point is null, then skip it
                if (oldPoint == null) {
                    continue;
                }
                float distance = newPoint.sub(oldPoint).mag();
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestIndex = j;
                }
            }
            // only add the new point if it either:
            // 1. has a closest point index which does not already exist in the correspondence matrix
            // 2. has a closest point index which already exists in the correspondence matrix, but the distance is smaller than the existing distance
            // In case 2, we want to replace the old point with the new point
            if (closestIndex != -1) {
                if (correspondenceMatrix.get(0).contains((float) closestIndex)) {
                    int oldIndex = correspondenceMatrix.get(0).indexOf((float) closestIndex);
                    if (correspondenceMatrix.get(2).get(oldIndex) > closestDistance) {
                        correspondenceMatrix.get(0).set(oldIndex, (float) closestIndex);
                        correspondenceMatrix.get(1).set(oldIndex, (float) newPointIndex);
                        correspondenceMatrix.get(2).set(oldIndex, closestDistance);
                    }
                } else {
                    correspondenceMatrix.get(0).add((float) closestIndex);
                    correspondenceMatrix.get(1).add((float) newPointIndex);
                    correspondenceMatrix.get(2).add(closestDistance);
                }
            }
        }
    }
}

class ScanMatcher{
    // A 2x2 matrix describing a rotation to apply to the new scan
    SimpleMatrix rotationMatrix;

    // A 2x1 matrix describing a translation to apply to the new scan
    SimpleMatrix translationVector;

    ScanMatcher(){
    }

    /**
     * @brief Compute the average position of the scan
     * @param scan the scan to compute the average position of
     * @return a 2x1 matrix containing the x,y coordinates of the average position of the scan
     */
    private SimpleMatrix averageScanPosition(ScanPoint scan){
        Vector averagePosition = new Vector(0, 0);
        int invalidPoints = 0;
        for (Vector point : scan.getScan()) {
            if (point != null) {
                averagePosition = averagePosition.add(point);
            }
            else{
                invalidPoints++;
            }
        }
        return new SimpleMatrix(averagePosition.div(scan.getScan().size() - invalidPoints).toArray());
    }

    /**
     * @brief Compute the cross covariance matrix between the new scan and the reference scan
     * @return a 2x2 matrix containing the cross covariance matrix
     */
    private SimpleMatrix crossCovarianceMatrix(ScanPoint referenceScan, ScanPoint newScan){
        Vector referenceScanAveragePosition = new Vector(averageScanPosition(referenceScan));
        Vector newScanAveragePosition = new Vector(averageScanPosition(newScan));

        CorrespondenceMatrix correspondenceMatrix = new CorrespondenceMatrix(newScan, referenceScan);

        // compute the cross covariance matrix which is given by the formula:
        // covariance = the sum from 1 to N of (p_i) * (q_i)^T
        // where p_i is the ith point in the new scan and q_i is the ith point in the reference scan and N is the number of points in the scan
        // the cross covariance matrix is a 2x2 matrix
        float[][] crossCovarianceMatrix = new float[2][2];
        for (int i = 0; i < correspondenceMatrix.getOldPointIndices().size(); i++) {
            int oldIndex = correspondenceMatrix.getOldPointIndices().get(i);
            int newIndex = correspondenceMatrix.getNewPointIndices().get(i);
            Vector oldPoint = referenceScan.getScan().get(oldIndex);
            Vector newPoint = newScan.getScan().get(newIndex);
            if (oldPoint != null && newPoint != null) {
                Vector oldPointCentered = oldPoint.sub(referenceScanAveragePosition);
                Vector newPointCentered = newPoint.sub(newScanAveragePosition);
                crossCovarianceMatrix[0][0] += oldPointCentered.x * newPointCentered.x;
                crossCovarianceMatrix[0][1] += oldPointCentered.x * newPointCentered.y;
                crossCovarianceMatrix[1][0] += oldPointCentered.y * newPointCentered.x;
                crossCovarianceMatrix[1][1] += oldPointCentered.y * newPointCentered.y;
            }
        }
        return new SimpleMatrix(crossCovarianceMatrix);
    }

    /**
     * @brief Compute the rotation and translation matrices between the new scan and the reference scan. Then cache them as private variables.
     * The rotation matrix is a 2x2 matrix and the translation vector is a 2x1 matrix
     */
    public void calculateRotationAndTranslationMatrices(ScanPoint referenceScan, ScanPoint newScan){
        // compute the rotation matrix which is given by the formula:
        // R = V * U^T
        // where V and U are the singular value decomposition of the cross covariance matrix
        // the rotation matrix is a 2x2 matrix
        SimpleMatrix crossCovarianceMatrixSimple = crossCovarianceMatrix(referenceScan, newScan);
        SimpleSVD<SimpleMatrix> svd = crossCovarianceMatrixSimple.svd();
        this.rotationMatrix = svd.getU().mult(svd.getV().transpose());

        SimpleMatrix newScanAveragePosition = averageScanPosition(newScan);
        SimpleMatrix referenceScanAveragePosition = averageScanPosition(referenceScan);
        this.translationVector = referenceScanAveragePosition.minus(rotationMatrix.mult(newScanAveragePosition));
    }

    public SimpleMatrix getRotationMatrix(){
        return this.rotationMatrix;
    }

    public SimpleMatrix getTranslationVector(){
        return this.translationVector;
    }

    public ScanPoint applyRotationAndTranslationMatrices(ScanPoint newScan){
        // apply the rotation matrix and translation vector to the new scan
        for (int i = 0; i < newScan.getScan().size(); i++) {
            Vector point = newScan.getScan().get(i);
            if (point != null) {
                SimpleMatrix pointMatrix = new SimpleMatrix(point.toArray());
                SimpleMatrix newPointMatrix = rotationMatrix.mult(pointMatrix).plus(translationVector);
                newScan.getScan().set(i, new Vector((float) newPointMatrix.get(0), (float) newPointMatrix.get(1)));
            }
        }
        return newScan;
    }

    public float getError(ScanPoint referenceScan, ScanPoint newScan){
        // calculate the error between the new scan and the reference scan
        // q is reference scan and p is new scan
        // error is given as abs(Q_mean - R * P_mean)
        // where Q_mean is the average position of the reference scan
        // P_mean is the average position of the new scan
        // R is the rotation matrix

        SimpleMatrix newScanAveragePosition = averageScanPosition(newScan);
        SimpleMatrix referenceScanAveragePosition = averageScanPosition(referenceScan);
        SimpleMatrix error = referenceScanAveragePosition.minus(rotationMatrix.mult(newScanAveragePosition));
        return (float) error.elementSum();
    }
}
