import Vector.*;

import processing.core.PApplet;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Ray extends Line {
    float maxRayDistance = 1000;
    int[] color = new int[]{255, 255, 255};
    //takes the starting position of the ray, the length of the ray, and it's casting angle (radians)
    Ray(Vector startPosition, float angle){
        super(startPosition, startPosition.add(new Vector(cos(angle), sin(angle))));
        direction = direction.mul(maxRayDistance);
    }

    public void drawRay(PApplet proc){
        proc.stroke(color[0], color[1], color[2]);
        proc.line(position.x, position.y, position.x + direction.x, position.y + direction.y);
//        proc.noFill();
//        proc.circle(position.x, position.y, 2*direction.mag());
//        proc.fill(255);
    }



    //checks to see at what coordinate the ray will collide with an object and sets the ray length to meet that point.
    public void castRay(ArrayList<Wall> walls){
        float shortestWallDistance = maxRayDistance;
        int[] newColor = new int[]{255, 255, 255};
        for(Wall wall : walls){

            // get the necessary vectors for two parameterized lines
            // parameterized lines are of the form L = d*t + p
            Vector d1 = this.direction.normalize().mul(maxRayDistance);
            Vector d2 = wall.getDirection();
            Vector p1 = this.position;
            Vector p2 = wall.getPosition();

            // calculate the parameters for the intersection t and u
            float t = -(d2.x*(p2.y-p1.y) + d2.y*(p1.x-p2.x))/(d1.x*d2.y - d2.x*d1.y);
            float u = -(d1.x*(p2.y-p1.y) + d1.y*(p1.x-p2.x))/(d1.x*d2.y-d2.x*d1.y);

            // the lines will only be intersecting when both t and u are between 0 and 1.
            if(!(0 <= t && t <= 1 && 0 <= u && u <= 1)){
                continue;
            }

            // if the distance from the ray to the intersection is shorter than the shortestWallDistance, this is our new closest wall
            float distance = d1.mul(t).add(p1).sub(this.position).mag();
            if(distance < shortestWallDistance){
                shortestWallDistance = distance;
                newColor = new int[]{wall.r, wall.g, wall.b};
            }

        }

        // if we collided with a wall, set the ray's length to the distance from it to the collision
        if(shortestWallDistance != maxRayDistance){
            this.direction = this.direction.normalize().mul(shortestWallDistance);
            this.color = newColor;
        }
        else{
            this.direction = this.direction.normalize().mul(maxRayDistance);
            this.color = new int[]{255, 255, 255};
        }
    }

    public Vector getPos(){ return this.position;}

    public float getRayLength(){return this.direction.mag();}


    public boolean hasCollided(){
        return abs(this.direction.mag() - maxRayDistance) > 0.001;
    }

    //returns the absolute position of the point
    public Vector getPoint(){
        if(this.direction.mag() == 0){
            return this.position;
        }

        return this.position.add(this.direction);
    }

    public void setPos(Vector newPosition){
        this.position = newPosition;
    }

    public void setRayLength(int rayLength){this.direction = this.direction.normalize().mul(rayLength);}

    public void setAngle(float angle){
        float distance = this.direction.mag();
        this.direction = new Vector(cos(angle), sin(angle)).mul(distance);
    }

}