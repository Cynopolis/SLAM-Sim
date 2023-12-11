package ScanGraph;

import Graph.Vertex;
import Vector.Vector;
import org.ejml.simple.SimpleMatrix;

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
        this.orientation = other.getAngle();
        this.scan = new ArrayList<>(other.getPoints());
    }

    /**
     * @return a two eleement float array containing the x and y coordinates of the vertex respectively.
     */
    public Vector getPos(){
        return position;
    }

    public void setPos(Vector pos){
        this.position = pos;
    }

    public float getAngle(){
        return this.orientation;
    }

    public ArrayList<Vector> getPoints(){
        return this.scan;
    }

    /**
     * @brief Update the pose of the scan point
     * @param rotation The rotation matrix to apply to the scan point
     * @param translation The translation matrix to apply to the scan point
     */
    public void UpdatePose(SimpleMatrix rotation, SimpleMatrix translation){
        SimpleMatrix pose = new SimpleMatrix(3,1);
        pose.set(0,0, this.position.x);
        pose.set(1,0, this.position.y);
        pose.set(2,0, this.orientation);
        SimpleMatrix newPose = translation.plus(rotation.mult(pose));
        this.position.x = (float)newPose.get(0,0);
        this.position.y = (float)newPose.get(1,0);
        this.orientation = (float)newPose.get(2,0);
    }

}
