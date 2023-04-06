import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Ray extends Line{
    //takes the starting position of the ray, the length of the ray, and it's casting angle (radians)
    Ray(Vector startPosition, float angle){
        super(startPosition, startPosition.add(new Vector(cos(angle), sin(angle))));
    }

    public void drawRay(PApplet proc){
        proc.line(position.x, position.y, position.add(direction).x, position.add(direction).y);
    }

    //checks to see at what coordinate the ray will collide with an object and sets the ray length to meet that point.
    public void castRay(ArrayList<Wall> walls){
        float maxRange = 1000;
        for(Wall wall : walls){
            // TODO: find the intersection between the wall and ray.
        }
    }

    public Vector getPos(){ return this.position;}

    public float getRayLength(){return this.direction.mag();}

    public float getAngle(){return atan2(this.direction.y, this.direction.x);}

    public boolean hasCollided(){
        return this.direction.mag() == 0;
    }

    //returns the absolute position of the point
    public Vector getPoint(){
        if(this.direction.mag() == 0){
            return new Vector();
        }

        return this.position.add(this.direction);
    }

    public void setPos(Vector newPosition){
        this.position = newPosition;
    }

    public void setRayLength(int rayLength){this.direction = this.direction.normalize().mul(rayLength)}

    public void setAngle(float angle){
        float distance = this.direction.mag();
        this.direction = new Vector(cos(angle), sin(angle)).mul(distance);
    }

}