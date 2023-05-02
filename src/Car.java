import java.util.ArrayList;

import static java.lang.Math.PI;
import static processing.core.PApplet.degrees;
import static processing.core.PApplet.radians;

import Vector.Vector;
import processing.core.PApplet;

public class Car{
    Vector pose = new Vector(0,0); // the car's x, y position
    float angle = 0; // the current angle that the car is at.
    int carLength = 50;
    int carWidth = 40;
    SLAM slam;
    private static PApplet proc;

    ArrayList<View> views = new ArrayList<>();

    // default constructor
    Car(PApplet processing){
        proc = processing;
        slam = new SLAM(proc);
    }

    Car(PApplet processing, int xPos, int yPos, int carLength, int carWidth){
        proc = processing;
        slam = new SLAM(proc);
        this.pose = new Vector(xPos, yPos);
        this.carLength = carLength;
        this.carWidth = carWidth;
    }

    //adds a new view with the specified FOV and ray number
    public void addView(float FOV, int numberOfRays){
        views.add(new View(proc, pose, numberOfRays, radians(FOV)));
    }

    //draw the car and its views
    public void drawCar(ArrayList<Wall> walls){
        proc.stroke(255);
        proc.ellipse(pose.x, pose.y, carWidth, carLength);
        this.updateScan(walls);
//        this.slam.drawLines();
    }

    //With all the views that the car has, get their point list
    void updateScan(ArrayList<Wall> walls){
        for(View view : views){
            view.look(walls);
        }

        for(View view : views){
            ArrayList<Vector> pointList = view.getPoints();
//            slam.RANSAC(pointList, view.getFOV() / view.getRayNum());
        }
    }

    public Vector getPose(){
        return pose;
    }

    //always returns a positive angle between 0 and 360 degrees
    public float getAngle(){
        return degrees(this.angle);
    }

    //the given angle is in DEGREES!
    public void setAngle(float angle){
        //converts from degrees to radians
        angle = radians(angle);
        while(angle >= 2*PI){angle -= 2*PI;}
        while(angle <= -2*PI){angle += 2*PI;}
        this.angle = angle;
        for(View view : views){
            view.setAngle(angle);
        }
    }

    public void setPose(Vector newPose){
        pose = newPose;
        for(View view : views){
            view.setPos(pose);
        }
    }
}