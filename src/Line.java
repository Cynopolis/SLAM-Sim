import processing.core.PApplet;

import java.util.List;

import static processing.core.PApplet.*;

public class Line{
    private Vector direction = new Vector(0,0);
    private Vector position = new Vector(0,0);

    Line(Vector startPosition, Vector endPosition){
        direction = endPosition.sub(startPosition);
        position = startPosition;
    }

    /**
     * attempt to find the line of best fit for the given points
     * @param points the points to get the line of best for
     */
    Line(List<Vector> points){
        bestFit(points);
    }

    // least squares line of best fit algorithm
    private void bestFit(List<Vector> points){
        // get the mean of all the points
        Vector mean = new Vector();
        for(Vector point : points){
            mean = mean.add(point);
        }
        mean = mean.div(points.size());

        // this section calculates the direction vector of the line of best fit
        Vector direction = new Vector();
        float length = 1;
        // get the rise and run of the line of best fit
        for(Vector point : points){
            direction.y += (point.x - mean.x)*(point.y - mean.y); // rise
            direction.x += pow((point.x - mean.x),2);

            // find the point that's furthest from the mean and use it to set the line length.
            float dist = abs(point.sub(mean).mag());
            if(dist > length){
                length = 2*dist;
            }

        }
        if(direction.y == 0){
            this.direction = new Vector(0, 1);
        }
        else{
            this.direction = new Vector(1, direction.y/direction.x);
        }
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

    public void draw(PApplet screen){
        Vector endPoint = this.position.add(this.direction);
        screen.line(position.x, position.y, endPoint.x, endPoint.y);
    }

    /**
     * @param point
     * @return the smallest distance from the point to this line
     */
    public float getDistance(Vector point){
        return (point.sub(position).cross(direction)).mag() / direction.mag();
    }
}