import processing.core.*;

import java.util.ArrayList;

import static processing.core.PApplet.pow;

public class SLAM extends PApplet{
    ArrayList<PVector> points = new ArrayList<PVector>();

    SLAM(){
    }

    public void addPoints(ArrayList<PVector> newPoints){
        Line line = new Line(newPoints);
    }

}

class Line{
    PVector direction = new PVector(0,0);
    PVector position = new PVector(0,0);

    Line(){}
    Line(PVector direction, PVector position){
        this.direction = direction;
        this.position = position;
    }

    /**
     * @brief attempt to find the line of best fit for the given points
     * @param points the points to get the line of best for
     */
    Line(ArrayList<PVector> points){
        bestFit(points);
    }

    // least squares line of best fit algorithm
    private void bestFit(ArrayList<PVector> points){
        // get the mean of all the points
        PVector mean = new PVector();
        for(PVector point : points){
            mean.add(point);
        }
        mean.div(points.size());

        // this section calculates the direction vector of the line of best fit
        PVector direction = new PVector();
        // get the rise and run of the line of best fit
        for(PVector point : points){
            direction.y += (point.x - mean.x)*(point.y - mean.y); // rise
            direction.x += pow((point.x - mean.x),2);
        }

        this.position = mean;
        this.direction = direction;
    }

    public PVector getSlopeIntForm(){
        float slope = direction.y / direction.x;
        float intercept = position.y - slope * position.x;
        return new PVector(slope, intercept);
    }

    public PVector getDirection(){
        return direction;
    }

    public PVector getPosition(){
        return position;
    }
}