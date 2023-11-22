package ScanGraph;

import Graph.PointGraph;
import Graph.Vertex;
import Vector.Vector;
import processing.core.PApplet;

import java.util.ArrayList;

public class PointScan extends Vertex{

    private Vector position;
    private ArrayList<Vector> scan;

    PointScan(Vector scanPosition, ArrayList<Vector> scan){
        super();
        this.position = scanPosition;
        this.scan = scan;
    }

    /**
     * @param x the new x position of the vertex
     * @param y the new y posiiton of the vertex
     */
    public void setPos(float x, float y){
        this.position = new Vector(x, y);
    }

    /**
     * @return a two eleement float array containing the x and y coordinates of the vertex respectively.
     */
    public Vector getPos(){
        return position;
    }

    public ArrayList<Vector> getScan(){
        return this.scan;
    }

}
