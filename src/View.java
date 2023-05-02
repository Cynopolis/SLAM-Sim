import Vector.Vector;
import processing.core.*;
import java.util.ArrayList;

public class View {
    Vector pose;
    float angle = 0;
    float FOV;
    ArrayList<Ray> rays = new ArrayList<>();
    private static PApplet proc;

    //the x,y position of the view, what angle it's looking at and its FOV
    View(PApplet processing, Vector newPose, int numberOfRays, float FOV) {
        proc = processing;
        this.pose = newPose;
        this.FOV = FOV;
        this.setRayNum(numberOfRays, FOV, this.angle);
    }

    //sets the number of rays and their starting values in the ray list
    public void setRayNum(int numberOfRays, float FOV, float angleOffset) {
        float rayStep = FOV / numberOfRays;
        rays.clear();
        float angle = (float) (0.01 - angleOffset); //the 0.01 fixes some bugs
        for (int i = 0; i < numberOfRays; i++) {
            Ray ray = new Ray(pose, angle);
            angle = angle + rayStep;
            rays.add(ray);
        }
    }

    //sees if the ray will collide with the walls in the wall list
    public void look(ArrayList<Wall> walls) {
        for (Ray ray : rays) {
            ray.castRay(walls);
            if(ray.hasCollided()){
                ray.drawRay(proc);
            }
        }
    }

    //changes the position of the view
    public void setPos(Vector newPose) {
        pose = newPose;
        for (Ray ray : rays) {
            ray.setPos(pose);
        }
    }

    //changes the angle of the view
    public void setAngle(float angle) {
        this.angle = angle;
        this.setRayNum(rays.size(), this.FOV, angle);
    }

    //changes the field of view of the view
    public void setFOV(float FOV) {
        this.FOV = FOV;
        this.setRayNum(this.rays.size(), this.FOV, this.angle);
    }

    public Vector getPos() {
        return pose;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getFOV() {
        return this.FOV;
    }

    public int getRayNum() {
        return this.rays.size();
    }

    //gets the point that each ray has collided with
    public ArrayList<Vector> getPoints() {
        ArrayList<Vector> points = new ArrayList<>();

        for (Ray ray : rays) {
            if (ray.hasCollided()){
                points.add(ray.getPoint());
            }
        }
        return points;
    }
}

