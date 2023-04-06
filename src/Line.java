import processing.core.PApplet;

import java.util.List;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.pow;

public class Line{
    Vector direction = new Vector(0,0);
    Vector position = new Vector(0,0);
    float length = 0;
    private static PApplet proc;

    Line(PApplet processing, Vector startPosition, Vector endPosition){
        this.proc = processing;
        this.position = startPosition;
        this.direction = endPosition.sub(startPosition).normalize();
        this.length = direction.mag();
    }
    Line(PApplet processing, Vector direction, Vector position, float lineLength){
        this.direction = direction.normalize();
        this.position = position;
        this.length = lineLength;
        proc = processing;
    }

    /**
     * attempt to find the line of best fit for the given points
     * @param points the points to get the line of best for
     */
    Line(PApplet processing, List<Vector> points){
        bestFit(points);
        proc = processing;
    }

    // least squares line of best fit algorithm
    private void bestFit(List<Vector> points){
        // get the mean of all the points
        Vector mean = new Vector();
        for(Vector point : points){
            mean.add(point);
        }
        mean.div(points.size());

        // this section calculates the direction vector of the line of best fit
        Vector direction = new Vector();

        // get the rise and run of the line of best fit
        for(Vector point : points){
            direction.y += (point.x - mean.x)*(point.y - mean.y); // rise
            direction.x += pow((point.x - mean.x),2);

            // find the point that's furthest from the mean and use it to set the line length.
            float dist = abs(point.sub(mean).mag());
            if(dist > this.length){
                this.length = 2*dist;
            }

        }

        this.position = mean.sub(direction.div(direction.mag()).mul(this.length / 2));
        this.direction = direction.normalize();
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
        return length;
    }

    /**
     * Draw the line on screen
     */
    public void draw(){
        Vector endPoint = position.add(direction.mul(length));
        proc.line(position.x, position.y, endPoint.x, endPoint.y);
    }

    /**
     * @param point
     * @return the smallest distance from the point to this line
     */
    public float getDistance(Vector point){
        return (point.sub(position).cross(direction)).mag() / direction.mag();
    }
}