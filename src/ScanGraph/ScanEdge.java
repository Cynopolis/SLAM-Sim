package ScanGraph;

import Graph.Edge;
import Vector.Line;
import Vector.LineInterface;
import Vector.Vector;
import processing.core.PApplet;

import static java.lang.Math.PI;

public class ScanEdge extends Edge {
    protected Line line;

    // Additional properties specific to scan edges
    private boolean isLoopClosure = false;

    /**
     * @brief Constructor for a scan edge
     * @param vStart the starting vertex
     * @param vEnd   the ending vertex
     */
    public ScanEdge(ScanPoint vStart, ScanPoint vEnd){
        super(vStart, vEnd);

        this.line = new Line(vStart.getPos(), vEnd.getPos());
    }

    /**
     * @brief Constructor for a scan edge
     * @param vStart the starting vertex
     * @param vEnd   the ending vertex
     * @param weight the weight of the edge
     */
    public ScanEdge(ScanPoint vStart, ScanPoint vEnd, float weight) {
        super(vStart, vEnd, weight);
        this.line = new Line(vStart.getPos(), vEnd.getPos());
    }

    // Getter and setter for loop closure flag
    public boolean isLoopClosure() {
        return isLoopClosure;
    }

    public void setLoopClosure(boolean loopClosure) {
        isLoopClosure = loopClosure;
    }
}
