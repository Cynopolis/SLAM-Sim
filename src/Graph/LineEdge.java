package Graph;

import Vector.*;
import processing.core.PApplet;

import java.awt.*;

import static java.lang.Math.PI;

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
        Vector leftFlange = line.getDirection().rotate2D((float)(-3*PI/4)).normalize().mul(20);
        Vector rightFlange = line.getDirection().rotate2D((float) (3*PI/4)).normalize().mul(20);
        Line l1 = new Line(line.endPoint(), line.endPoint().add(leftFlange));
        Line l2 = new Line(line.endPoint(), line.endPoint().add(rightFlange));
        l1.draw(proc);
        l2.draw(proc);
    }

}
