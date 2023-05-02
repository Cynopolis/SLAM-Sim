package Graph;

import Vector.*;
import processing.core.PApplet;

import java.awt.*;

public class LineEdge extends Edge implements LineInterface{
    protected PointVertex vStart;
    protected PointVertex vEnd;
    protected Line line;
    public LineEdge(PointVertex vStart, PointVertex vEnd) {
        this.vStart = vStart;
        this.vEnd = vEnd;
        this.line = new Line(vStart.getPos(), vEnd.getPos());
    }

    public Vector getDirection(){
        return line.getDirection();
    }

    public Vector getPosition(){
        return line.getPosition();
    }

    public float getLength(){
        return line.getLength();
    }

    public float getAngle(){
        return line.getAngle();
    }

    public Vector endPoint(){
        return line.endPoint();
    }

    public float getDistance(Vector point){
        return line.getDistance(point);
    }

    public void draw(PApplet proc){
        line.draw(proc);
    }
}
