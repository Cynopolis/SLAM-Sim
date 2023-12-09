package ScanGraph;

import Vector.Vector;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * @brief A class that can match two point scans together
 */
public class ScanMatcher{
    // A 2x2 matrix describing a rotation to apply to the new scan
    public SimpleMatrix rotationMatrix = null;

    // A 2x1 matrix describing a translation to apply to the new scan
    public SimpleMatrix translationVector = null;

    public ScanMatcher(){
    }

    /**
     * @brief iteratively calculate new rotation and transpose matrices to determien if the two scans match
     * @param referenceScan the scan to be referenced
     * @param newScan the scan that will be rotated and moved until it matches the reference scan
     * @param iterations The number of iterations that the scan matcher will attempt
     * @param errorThreshold The error threshold that the match will have to meet before considering it a valid match
     */
    public ScanPoint iterativeScanMatch(ScanPoint referenceScan, ScanPoint newScan, float errorThreshold, int iterations){
        // calculate the rotation and translation matrices between the new scan and the reference scan
        this.calculateRotationAndTranslationMatrices(referenceScan, newScan);

        // copy the new scan so we don't modify the original
        ScanPoint matchingScan = new ScanPoint(newScan);

        SimpleMatrix lastRotationMatrix;
        SimpleMatrix lastTranslationVector;

        for (int i = 0; i < iterations; i++) {
            // update the new scan with the rotation matrix and translation vector
            matchingScan = this.applyRotationAndTranslationMatrices(matchingScan);

            // calculate the error between the new scan and the reference scan
            float error = this.getError(referenceScan, matchingScan);

            // if the error is less than some threshold, then we have found a match
            if (error < errorThreshold) {
                return referenceScan;
            }

            // cache the last rotation and translation matrices
            lastRotationMatrix = new SimpleMatrix(this.rotationMatrix);
            lastTranslationVector = new SimpleMatrix(this.translationVector);

            // calculate the rotation and translation matrices between the new scan and the reference scan
            this.calculateRotationAndTranslationMatrices(referenceScan, matchingScan);

            // combine the last rotation and translation matrices with the new rotation and translation matrices
            this.rotationMatrix = this.rotationMatrix.mult(lastRotationMatrix);
            this.translationVector = this.translationVector.plus(lastTranslationVector);
        }

        return null;
    }
    /**
     * @brief Compute the average position of the scan
     * @param scan the scan to compute the average position of
     * @return a 2x1 matrix containing the x,y coordinates of the average position of the scan
     */
    private SimpleMatrix averageScanPosition(ScanPoint scan){
        Vector averagePosition = new Vector(0, 0);
        int invalidPoints = 0;
        for (Vector point : scan.getPoints()) {
            if (point != null) {
                averagePosition = averagePosition.add(point);
            }
            else{
                invalidPoints++;
            }
        }
        return new SimpleMatrix(averagePosition.div(scan.getPoints().size() - invalidPoints).toArray());
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
            Vector oldPoint = referenceScan.getPoints().get(oldIndex);
            Vector newPoint = newScan.getPoints().get(newIndex);
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
        // copy the new scan so we don't modify the original
        ScanPoint tempScan = new ScanPoint(newScan);
        // apply the rotation matrix and translation vector to the new scan
        for (int i = 0; i < tempScan.getPoints().size(); i++) {
            Vector point = tempScan.getPoints().get(i);
            if (point != null) {
                SimpleMatrix pointMatrix = new SimpleMatrix(point.toArray());
                SimpleMatrix newPointMatrix = rotationMatrix.mult(pointMatrix).plus(translationVector);
                tempScan.getPoints().set(i, new Vector((float) newPointMatrix.get(0), (float) newPointMatrix.get(1)));
            }
        }
        return tempScan;
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
        return (float) abs(error.elementSum());
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
    private void calculateCorrespondenceMatrix(ScanPoint newScan, ScanPoint referenceScan) {
        for (int newPointIndex = 0; newPointIndex < newScan.getPoints().size(); newPointIndex++) {
            Vector newPoint = newScan.getPoints().get(newPointIndex);

            // Skip null points in the new scan
            if (newPoint == null) {
                continue;
            }

            float closestDistance = Float.MAX_VALUE;
            int closestIndex = -1;

            for (int oldPointIndex = 0; oldPointIndex < referenceScan.getPoints().size(); oldPointIndex++) {
                Vector oldPoint = referenceScan.getPoints().get(oldPointIndex);

                // Skip null points in the old scan
                if (oldPoint == null) {
                    continue;
                }

                float distance = newPoint.sub(oldPoint).mag();

                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestIndex = oldPointIndex;
                }
            }

            // Add the correspondence if a closest point is found
            if (closestIndex != -1) {
                this.oldPointIndices.add(closestIndex);
                this.newPointIndices.add(newPointIndex);
                this.distances.add(closestDistance);
            }
        }
    }
}