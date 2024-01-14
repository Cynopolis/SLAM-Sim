import Graph.PointGraph;
import Vector.Vector;
import processing.core.*;
import java.util.ArrayList;
import ScanGraph.ScanPoint;

public class View {
    Vector position;
    float angle = 0;
    float FOV;
    ArrayList<Ray> rays = new ArrayList<>();
    private static PApplet proc;

    /**
     * @brief Constructor for the View class
     * @param processing The PApplet that the view will be drawn on
     * @param newPose The position of the view
     * @param numberOfRays The number of rays that the view will have
     * @param FOV The field of view of the view
     */
    View(PApplet processing, Vector newPose, int numberOfRays, float FOV) {
        proc = processing;
        this.position = newPose;
        this.FOV = FOV;
        this.setRayNum(numberOfRays, FOV, this.angle);
    }

    //sets the number of rays and their starting values in the ray list
    public void setRayNum(int numberOfRays, float FOV, float angleOffset) {
        float rayStep = FOV / numberOfRays;
        rays.clear();
        float angle = (float) (angleOffset); //the 0.01 fixes some bugs
        for (int i = 0; i < numberOfRays; i++) {
            Ray ray = new Ray(position, angle);
            angle = angle + rayStep;
            rays.add(ray);
        }
    }

    /**
     * @brief Calculates the points of intersection of the rays with the map
     * @param map The map that the view is looking at
     */
    public void calculatePointScan(PointGraph map) {
        for (Ray ray : rays) {
            ray.castRay(map);
            if(ray.hasCollided()){
                ray.getPoint().draw(proc);
            }
        }
    }

    /**
     * @brief Sets the position of the view
     * @param newPosition The new position of the view
     */
    public void setPos(Vector newPosition) {
        position = newPosition;
        for (Ray ray : rays) {
            ray.setPos(position);
        }
    }

    /**
     * @brief Sets the angle of the view
     * @param newAngle The new angle of the view
     */
    public void setAngle(float newAngle) {
        this.angle = newAngle;
        for(Ray ray : rays){
            float angleOffset = ray.getAngle() - this.angle;
            ray.setAngle(this.angle+angleOffset);
        }
    }

    //changes the field of view of the view
    public void setFOV(float FOV) {
        this.FOV = FOV;
        this.setRayNum(this.rays.size(), this.FOV, this.angle);
    }

    /**
     * @return The position of the view
     */
    public Vector getPos() {
        return position;
    }

    /**
     * @return The angle of the view
     */
    public float getAngle() {
        return this.angle;
    }

    /**
     * @return The field of view of the view
     */
    public float getFOV() {
        return this.FOV;
    }

    /**
     * @return The number of rays that the view has
     */
    public int getRayNum() {
        return this.rays.size();
    }

    /**
     * @brief Get the most recent scan from the view
     * @return A ScanPoint object containing the position, angle and points of the view
     */
    public ScanPoint getScan() {
        ArrayList<Vector> points = new ArrayList<>();

        for (Ray ray : rays) {
            if(ray.hasCollided()){
                Vector point = ray.getPoint();
                // store the angle information for that point in the z coordinate
                point.z = ray.getAngle();
                points.add(point);
            }
        }
        return new ScanPoint(this.position,this.angle, points);
    }

    /**
     * @return A list of the angles where a collision was detected
     */
    public ArrayList<Float> getAngles(){
        ArrayList<Float> angles = new ArrayList<>();
        for (Ray ray : rays) {
            if (ray.hasCollided()){
                angles.add(ray.getAngle());
            }
        }
        return angles;
    }
}

