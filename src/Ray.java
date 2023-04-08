import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PApplet.*;

public class Ray extends Line{
    float maxRayDistance = 1000;
    //takes the starting position of the ray, the length of the ray, and it's casting angle (radians)
    Ray(Vector startPosition, float angle){
        super(startPosition, startPosition.add(new Vector(cos(angle), sin(angle))));
        direction = direction.mul(maxRayDistance);
    }

    public void drawRay(PApplet proc){
        proc.line(position.x, position.y, position.add(direction).x, position.add(direction).y);
    }

    //checks to see at what coordinate the ray will collide with an object and sets the ray length to meet that point.
    public void castRay(ArrayList<Wall> walls){
        float shortestWallDistance = maxRayDistance;
        for(Wall wall : walls){
            Vector rayLine = this.getSlopeIntForm(); // stored as x:m, y:b
            Vector wallLine = wall.getSlopeIntForm(); // m, b

            // find the point where they intersect
            float x = (wallLine.y - rayLine.y) / (rayLine.x - wallLine.x);
            float y = rayLine.x * x + rayLine.y;
            Vector intersect = new Vector(x, y);

            // if the intersect doesn't actually lie on the wallLine, go to the next wall
            float distAlongLine = intersect.sub(wall.getPosition()).mag();
            float angleFromLine = wall.direction.angleDiff(intersect.sub(wall.position));
            if(distAlongLine > wall.getLength() && abs(angleFromLine) < 5*PI/180){
                continue;
            }

            // if the distance from the ray to the intersection is shorter than the shortestWallDistance, this is our new closest wall
            float distance = this.position.sub(intersect).mag();
            if(distance < shortestWallDistance){
                shortestWallDistance = distance;
            }

        }

        // if we collided with a wall, set the ray's length to the distance from it to the collision
        if(shortestWallDistance != maxRayDistance){
            this.direction = this.direction.normalize().mul(shortestWallDistance);
        }
        else{
            this.direction = this.direction.normalize().mul(maxRayDistance);
        }
    }

    public Vector getPos(){ return this.position;}

    public float getRayLength(){return this.direction.mag();}


    public boolean hasCollided(){
        return this.direction.mag() != maxRayDistance;
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

    public void setRayLength(int rayLength){this.direction = this.direction.normalize().mul(rayLength);}

    public void setAngle(float angle){
        float distance = this.direction.mag();
        this.direction = new Vector(cos(angle), sin(angle)).mul(distance);
    }

}