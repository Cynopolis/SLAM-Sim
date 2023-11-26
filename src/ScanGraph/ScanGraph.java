package ScanGraph;

import Graph.Graph;
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

    public void addEdge(ScanPoint vEnd) {
        addVertex(vEnd);
        ScanEdge edge = new ScanEdge(this.lastPoint, vEnd);
        adjList.get((Vertex) this.lastPoint).add(edge);

        this.lastPoint = vEnd;
    }

    /**
     * @param newScan the scan to match
     * @return null if no match can be found, or an existing scan the matches the new scan.
     * @brief Get a new scan in and try to match it with all other scans in the graph
     */
    private ScanPoint getAssociatedScan(ScanPoint newScan) {
        ScanMatcher matcher = new ScanMatcher();
        // go through all of our available scans and try to match the new scan with the old scans. If no match can be found return null
        for (Vertex v : adjList.keySet()) {
            ScanPoint referenceScan = (ScanPoint) v;
            for (int i = 0; i < 5; i++) {
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
