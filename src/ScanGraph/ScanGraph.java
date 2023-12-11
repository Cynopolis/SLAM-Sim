package ScanGraph;

import Graph.Graph;
import Graph.Edge;
import Graph.Vertex;
import Vector.Vector;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import java.util.ArrayList;
public class ScanGraph extends Graph {

    ScanPoint lastPoint;

    public ScanGraph(ScanPoint startingPoint) {
        super();
        this.lastPoint = startingPoint;
    }

    /**
     * @brief Add a new scan to the graph
     * @param newScan the new scan to add
     */
    public void addScan(ScanPoint newScan) {
        addVertex(newScan);

        // check if the new scan matches any of the existing scans
        MatchedScanTransform matchedScan = getAssociatedScan(newScan);
        if (matchedScan.scan != null) {
            // if it does, add a loop closure constraint
            addLoopClosureConstraint(this.lastPoint, matchedScan);
        }
        else{
            // if it doesn't match anything else, add an edge between the last scan and the new scan
            ScanEdge edge = new ScanEdge(this.lastPoint, newScan);
            adjList.get((Vertex) this.lastPoint).add(edge);
        }

        this.lastPoint = newScan;
    }

    /**
     * @param newScan the scan to match
     * @return null if no match can be found, or an existing scan the matches the new scan.
     * @brief Get a new scan in and try to match it with all other scans in the graph
     */
    private MatchedScanTransform getAssociatedScan(ScanPoint newScan) {
        ScanMatcher matcher = new ScanMatcher();
        ScanPoint matchedScan = null;
        // go through all of our available scans and try to match the new scan with the old scans. If no match can be found return null
        for (Vertex v : adjList.keySet()) {
            ScanPoint referenceScan = (ScanPoint) v;
            matchedScan = matcher.iterativeScanMatch(referenceScan, newScan, 0.00001F, 5);

            if(matchedScan != null){
                // apply the transformation to the new scan
                break;
            }
        }

        return new MatchedScanTransform(matcher.getRotationMatrix(), matcher.getTranslationVector(), matchedScan);
    }

    /**
     * @param referenceScan the existing scan in the graph
     * @param matchedScan the new scan that matches the reference scan
     */
    private void addLoopClosureConstraint(ScanPoint referenceScan, MatchedScanTransform matchedScan) {
        // Compute relative transformation between referenceScan and newScan
        // You need to implement the logic for computing the relative transformation

        // Create a loop closure edge and add it to the graph
        ScanEdge loopClosureEdge = new ScanEdge(referenceScan, matchedScan.scan);
        loopClosureEdge.setLoopClosure(true);  // Mark the edge as a loop closure
        adjList.get(referenceScan).add(loopClosureEdge);

        // Optimize the graph after adding the loop closure constraint
        optimizeGraph();
    }

    /**
     * Perform graph optimization using the Levenberg-Marquardt algorithm
     */
    private void optimizeGraph() {
        // Create a matrix for pose parameters (you may need to adjust the size based on your requirements)
        int numPoses = adjList.size();
        int poseDim = 3; // Assuming 3D poses
        SimpleMatrix poses = new SimpleMatrix(numPoses * poseDim, 1);

        // Populate the poses matrix with current pose estimates from the graph
        int i = 0;
        for (Vertex v : adjList.keySet()) {
            ScanPoint scan = (ScanPoint) v;
            Vector poseVector = scan.getPos(); // You need to implement the method to get the pose vector
            poses.set(i++, 0, poseVector.x);
            poses.set(i++, 0, poseVector.y);
            poses.set(i++, 0, scan.getAngle());
        }

        // TODO: Create a matrix for loop closure constraints (you need to implement this)
        SimpleMatrix loopClosureConstraints = computeLoopClosureConstraints();

        // Use Levenberg-Marquardt optimization to adjust poses
        SimpleSVD<SimpleMatrix> svd = poses.svd();
        SimpleMatrix U = svd.getU();
        SimpleMatrix S = svd.getW();
        SimpleMatrix Vt = svd.getV().transpose();

        // Adjust poses using loop closure constraints
        SimpleMatrix adjustment = Vt.mult(loopClosureConstraints).mult(U.transpose())
                .scale(0.01); // Adjust this factor based on your problem

        // Update the poses in the graph and handle loop closure edges
        i = 0;
        for (Vertex v : adjList.keySet()) {
            ScanPoint scan = (ScanPoint) v;
            scan.setPos(new Vector(adjustment.get(i++, 0), adjustment.get(i++, 0), adjustment.get(i++, 0)));

            // TODO: Handle loop closure edges
            for (Edge edge : adjList.get(v)) {
                ScanEdge scanEdge = (ScanEdge) edge;
                if (scanEdge.isLoopClosure()) {
                    // Update any additional information specific to loop closure edges
                    // For example, you might update the weight or perform other adjustments
                    // based on the loop closure information
                }
            }
        }
    }

    /**
     * @return Matrix representing loop closure constraints (you need to implement this)
     */
    private SimpleMatrix computeLoopClosureConstraints() {
        // Implement the logic to compute loop closure constraints
        // This matrix should represent the relative transformation between loop closure nodes
        // It may involve iterating through loop closure edges and extracting relevant information
        // You may use a similar structure to the poses matrix
        // For simplicity, this example assumes a 3D pose with translation only
        int numPoses = adjList.size();
        int poseDim = 3;
        SimpleMatrix loopClosureConstraints = new SimpleMatrix(numPoses * poseDim, 1);

        // Populate loopClosureConstraints matrix with relevant information

        return loopClosureConstraints;
    }
}

/**
 * @brief struct to hold the rotation and translation between two scans
 */
class MatchedScanTransform{
    public SimpleMatrix rotation;
    public SimpleMatrix translation;

    public ScanPoint scan;

    /**
     * @brief constructor
     * @param rotation the rotation between the two scans
     * @param translation the translation between the two scans
     * @param scan the scan that was matched
     */
    MatchedScanTransform(SimpleMatrix rotation, SimpleMatrix translation, ScanPoint scan){
        this.rotation = rotation;
        this.translation = translation;
        this.scan = scan;
    }
}