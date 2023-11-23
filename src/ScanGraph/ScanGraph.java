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

        // go through all of our available scans and try to match the new scan with the old scans. If no match can be found return null
        for (Vertex v : adjList.keySet()) {
            ScanPoint referenceScan = (ScanPoint) v;
            // p is the newScan and q is the referenceScan
            CorrespondenceMatrix correspondenceMatrix = new CorrespondenceMatrix(newScan, referenceScan);

            // compute the average position of the new scan
            Vector averagePosition = new Vector(0, 0);
            int invalidPoints = 0;
            for (Vector point : newScan.getScan()) {
                if (point != null) {
                    averagePosition = averagePosition.add(point);
                }
                else{
                    invalidPoints++;
                }
            }
            SimpleMatrix averagePositionVector = new SimpleMatrix(averagePosition.div(newScan.getScan().size() - invalidPoints).toArray());

            // compute the average position of the reference scan
            Vector averageReferencePosition = new Vector(0, 0);
            invalidPoints = 0;
            for (Vector point : referenceScan.getScan()) {
                if (point != null) {
                    averageReferencePosition = averageReferencePosition.add(point);
                }
                else{
                    invalidPoints++;
                }
            }
            SimpleMatrix averageReferencePositionVector = new SimpleMatrix(averageReferencePosition.div(referenceScan.getScan().size() - invalidPoints).toArray());

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
                    Vector oldPointCentered = oldPoint.sub(averageReferencePosition);
                    Vector newPointCentered = newPoint.sub(averagePosition);
                    crossCovarianceMatrix[0][0] += oldPointCentered.x * newPointCentered.x;
                    crossCovarianceMatrix[0][1] += oldPointCentered.x * newPointCentered.y;
                    crossCovarianceMatrix[1][0] += oldPointCentered.y * newPointCentered.x;
                    crossCovarianceMatrix[1][1] += oldPointCentered.y * newPointCentered.y;
                }
            }

            // convert the cross covariance matrix to a simple matrix from ejml
            SimpleMatrix crossCovarianceMatrixSimple = new SimpleMatrix(crossCovarianceMatrix);
            // perform the single value decomposition on the cross covariance matrix
            SimpleSVD svd = crossCovarianceMatrixSimple.svd();
            // get the rotation matrix from the svd
            SimpleMatrix rotationMatrix = (SimpleMatrix) svd.getU().mult(svd.getV().transpose());
            // get the translation vector from the svd
            SimpleMatrix translationVector = averageReferencePositionVector.minus(rotationMatrix.mult(averagePositionVector));

            // update the new scan with the rotation matrix and translation vector
            for (int i = 0; i < newScan.getScan().size(); i++) {
                Vector point = newScan.getScan().get(i);
                if (point != null) {
                    SimpleMatrix pointMatrix = new SimpleMatrix(point.toArray());
                    SimpleMatrix newPointMatrix = rotationMatrix.mult(pointMatrix).plus(translationVector);
                    newScan.getScan().set(i, new Vector((float) newPointMatrix.get(0), (float) newPointMatrix.get(1)));
                }
            }

            // calculate the error between the new scan and the reference scan
            float error = 0;
            for (int i = 0; i < correspondenceMatrix.getOldPointIndices().size(); i++) {
                int oldIndex = correspondenceMatrix.getOldPointIndices().get(i);
                int newIndex = correspondenceMatrix.getNewPointIndices().get(i);
                Vector oldPoint = referenceScan.getScan().get(oldIndex);
                Vector newPoint = newScan.getScan().get(newIndex);
                if (oldPoint != null && newPoint != null) {
                    error += correspondenceMatrix.getDistances().get(i);
                }
            }
            error /= correspondenceMatrix.getOldPointIndices().size();

            // if the error is less than some threshold, then we have found a match
            if (error < 0.1) {
                return referenceScan;
            }

            // TODO: iteratively update the scan up to 5 times before determining that there is no match.


        }

        return null;
    }

    private void singleValueDecomposition(float[][] matrix){
        // compute the single value decomposition of the matrix

        // matrix multiply the matrix by its transpose

    }

}


/**
 * @brief A class to hold the correspondence matrix between two scans
 * The correspondence matrix is a 3xN matrix where N is the number of valid points in the scan
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
            if (newPoint == null) {
                continue;
            }
            float closestDistance = Float.MAX_VALUE;
            int closestIndex = -1;
            for (int j = 0; j < referenceScan.getScan().size(); j++) {
                Vector oldPoint = referenceScan.getScan().get(j);
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
