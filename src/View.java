import processing.core.*;
import java.util.ArrayList;
import java.util.Objects;

import static processing.core.PApplet.*;

public class View{
    PVector pose;
    float angle = 0;
    float FOV;
    ArrayList<Ray> rays = new ArrayList<>();
    private static PApplet proc;

    //the x,y position of the view, what angle it's looking at and its FOV
    View(PApplet processing, PVector newPose, int numberOfRays, float FOV){
        proc = processing;
        this.pose = newPose;
        this.FOV = FOV;
        this.setRayNum(numberOfRays, FOV, this.angle);
    }

    //sets the number of rays and their starting values in the ray list
    public void setRayNum(int numberOfRays, float FOV, float angleOffset){
        float rayStep = FOV/numberOfRays;
        rays.clear();
        float angle = (float) (0.01-angleOffset); //the 0.01 fixes some bugs
        for(int i = 0; i < numberOfRays; i++){
            Ray ray = new Ray(proc, pose, 100000, angle);
            angle = angle + rayStep;
            rays.add(ray);
        }
    }

    //sees if the ray will collide with the walls in the wall list
    public void look(ArrayList<Wall> walls){
        for (Ray ray : rays){
            ray.castRay(walls);
            ray.drawRay();
        }
    }

    //changes the position of the view
    public void setPos(PVector newPose){
        pose = newPose;
        for(Ray ray : rays){ray.setPos(pose);}
    }

    //changes the angle of the view
    public void setAngle(float angle){
        this.angle = angle;
        this.setRayNum(rays.size(), this.FOV, angle);
    }

    //changes the field of view of the view
    public void setFOV(float FOV){
        this.FOV = FOV;
        this.setRayNum(this.rays.size(), this.FOV, this.angle);
    }

    public PVector getPos(){return pose;}

    public float getAngle(){return this.angle;}

    public float getFOV(){return this.FOV;}

    public int getRayNum(){return this.rays.size();}

    //gets the point that each ray has collided with
    public ArrayList<PVector> getPoints(){
        ArrayList<PVector> points = new ArrayList<>();

        for(Ray ray : rays){
            if(!Objects.equals(ray.getPoint(), new PVector(0, 0) {
            })){
                points.add(ray.getPoint());
            }
        }
        return points;
    }
}

class Ray{
    PVector pose;
    int rayLength;
    int defaultRayLength;
    float angle; // IN RADIANS
    private static PApplet proc;

    //takes the starting position of the ray, the length of the ray, and it's casting angle (radians)
    Ray(PApplet processing, PVector position, int defaultRayLength, float angle){
        proc = processing;
        this.pose = position;
        this.defaultRayLength = defaultRayLength;
        this.rayLength = defaultRayLength;
        this.angle = angle;
    }

    public void drawRay(){
        proc.line(pose.x, pose.y, (pose.x + cos(angle)*rayLength), (pose.y + sin(angle)*rayLength));
    }

    //checks to see at what coordinate the ray will collide with an object and sets the ray length to meet that point.
    public void castRay(ArrayList<Wall> objects){
        this.rayLength = defaultRayLength;
        ArrayList<Integer> distances = new ArrayList<>();
        //sees what objects it collides with
        for(Wall object : objects){
            float theta1 = angle;
            float theta2 = radians(object.getAngle());
            PVector wallPos = object.getPos();

            //finds where along the wall the ray collides
            float b = (pose.x*sin(theta1) + wallPos.y*cos(theta1) - pose.y*cos(theta1) - wallPos.x*sin(theta1)) / (cos(theta2)*sin(theta1) - sin(theta2)*cos(theta1));

            //if the place along the wall is further away than the wall extends, then it didn't collide
            if(b < object.getLength() && b > 0){
                //finds the length of the ray needed to collide with the wall
                float a = (b*sin(theta2) + wallPos.y-pose.y) / sin(theta1);
                //add that length to a list
                if(a > 0){
                    distances.add((int)abs(a));
                }

            }
        }
        //finds the shortest distance and sets the length of the ray to that distance
        if(distances.size() > 0){
            for(Integer distance : distances){
                if(distance < rayLength){
                    rayLength = distance;
                }
            }
        }
        else this.rayLength = defaultRayLength;
    }

    public PVector getPos(){ return pose;}

    public int getRayLength(){return this.rayLength;}

    public float getAngle(){return this.angle;}

    public boolean hasCollided(){
        return this.defaultRayLength != this.rayLength;
    }

    //returns the absolute position of the point
    public PVector getPoint(){
        if(this.rayLength != this.defaultRayLength){
            return new PVector(rayLength * (int)cos(this.angle) + pose.x, rayLength * (int)sin(this.angle) + pose.y);
        }
        else{
            return new PVector(0,0);
        }
    }

    public void setPos(PVector newPose){
        pose = newPose;
    }

    public void setRayLength(int rayLength){this.rayLength = rayLength;}

    public void setDefaultRayLength(int defaultRayLength){this.defaultRayLength = defaultRayLength;}

    public void setAngle(float angle){this.angle = angle;}

}

