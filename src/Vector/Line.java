package Vector;

import Vector.Vector;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.*;

public class Line implements LineInterface{
    // vector which represents  the direction and length of the line from its starting position
    protected Vector direction = new Vector(0,0);
    // store the starting position of the line
    protected Vector position = new Vector(0,0);

    protected ArrayList<Vector> points = new ArrayList<>();

    public Line(Vector startPosition, Vector endPosition){
        direction = endPosition.sub(startPosition);
        position = startPosition;
    }

    /**
     * attempt to find the line of best fit for the given points
     * @param points the points to get the line of best for
     */
    public Line(List<Vector> points){
        bestFit(points);
    }

    public void refitLine(Vector newPoint){
        // add the new point to our list
        this.points.add(newPoint);
        // rerun the bestFit algorithm with the new point
        bestFit(new ArrayList<>());
    }

    // least squares line of best fit algorithm
    public void bestFit(List<Vector> fitPoints){
        this.points.addAll(fitPoints);
        // get the mean of all the points
        Vector mean = new Vector();
        for(Vector point : points){
            mean = mean.add(point);
        }
        mean = mean.div(points.size());

        // this section calculates the direction vector of the line of best fit
        Vector direction = new Vector();
        float length = 0;
        // get the rise and run of the line of best fit
        for(Vector point : points){
            direction.y += (point.x - mean.x)*(point.y - mean.y); // rise
            direction.x += pow((point.x - mean.x),2);

            // get the average distance avery point is away from the mean.
            float dist = abs(point.sub(mean).mag());
            length += dist;

        }
        length = 2f*length/points.size();
        // if the direction is perfectly vertical create a line to represent that.
        if(direction.y == 0){
            this.direction = new Vector(0, 1);
        }
        else{
            this.direction = new Vector(1, direction.y/direction.x);
        }
        // scale the direction vector to be the correct length of the line.
        this.direction = this.direction.normalize().mul(length);

        this.position = mean.sub(this.direction.div(2));
    }

    public Vector getSlopeIntForm(){
        float slope = direction.y / direction.x;
        float intercept = position.y - slope * position.x;
        return new Vector(slope, intercept);
    }

    public Vector getDirection(){
        return direction;
    }

    public Vector getPosition(){
        return position;
    }

    public float getLength(){
        return direction.mag();
    }

    public float getAngle(){return atan2(this.direction.y, this.direction.x);}

    public Vector endPoint(){
        return this.position.add(this.direction);
    }

    public float getDistance(Vector point){
        float line_dist = direction.mag();
        if(line_dist == 0) return this.position.sub(point).mag();

        Vector l2 = this.endPoint();
        float t = ((point.x - position.x) * (l2.x - position.x) + (point.y - position.y) * (l2.y - position.y) + (point.z - position.z) * (l2.z - position.z)) / line_dist;
        t = constrain(t, 0, 1);
        Vector closestPoint = new Vector(position.x + t * (l2.x - position.x), position.y + t * (l2.y - position.y), position.z + t * (l2.z - position.z));
        return closestPoint.sub(point).mag();
    }

    public void draw(PApplet proc){
        proc.line(position.x, position.y, endPoint().x, endPoint().y);
    }
}