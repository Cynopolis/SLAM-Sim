package ScanGraph;

import Graph.Vertex;
import Vector.Vector;

import java.util.ArrayList;

public class ScanPoint extends Vertex{

    private Vector position;
    private Vector orientation;
    private ArrayList<Vector> scan;

    ScanPoint(Vector scanPosition, Vector orientation, ArrayList<Vector> scan) {
        super();
        this.position = scanPosition;
        this.orientation = orientation;
        this.scan = scan;
    }

    /**
     * @return a two eleement float array containing the x and y coordinates of the vertex respectively.
     */
    public Vector getPos(){
        return position;
    }

    public Vector getOrientation(){
        return this.orientation;
    }

    public ArrayList<Vector> getScan(){
        return this.scan;
    }

}
