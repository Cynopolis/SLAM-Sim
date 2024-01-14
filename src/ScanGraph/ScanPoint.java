package ScanGraph;

import Graph.Vertex;
import Vector.Vector;

import java.util.ArrayList;

public class ScanPoint extends Vertex{

    private Vector position;
    private float orientation;
    private ArrayList<Vector> scan;

    public ScanPoint(Vector scanPosition, float orientation, ArrayList<Vector> scan) {
        super();
        this.position = scanPosition;
        this.orientation = orientation;
        this.scan = scan;
    }

    /**
     * @brief Copy constructor
     * @param other The scan point to copy
     */
    public ScanPoint(ScanPoint other){
        super();
        this.position = new Vector(other.getPos().x, other.getPos().y);
        this.orientation = other.getOrientation();
        this.scan = new ArrayList<>(other.getPoints());
    }

    /**
     * @return a two eleement float array containing the x and y coordinates of the vertex respectively.
     */
    public Vector getPos(){
        return position;
    }

    public float getOrientation(){
        return this.orientation;
    }

    public ArrayList<Vector> getPoints(){
        return this.scan;
    }

}
