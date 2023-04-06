import processing.core.*;

import static processing.core.PApplet.*;

public class Wall{
    PVector pos;
    float angle;
    int wallLength;
    int r = (int)random(50, 255);
    int g = (int)random(50, 255);
    int b = (int)random(50, 255);

    Wall(PVector pos, float angle, int wallLength){
        this.pos = pos;
        this.angle = angle;
        this.wallLength = wallLength;
    }

    void drawWall(){
        stroke(r,g,b);
        line(pos.x, pos.y, (pos.x + cos(radians(angle))*wallLength), (pos.y + sin(radians(angle))*wallLength));
        //ellipse((xPos + cos(radians(angle))*wallLength), (yPos + sin(radians(angle))*wallLength), 20, 20);
    }

    PVector getPos(){
        return pos;
    }

    float getAngle(){
        return angle;
    }

    int getLength(){
        return wallLength;
    }
}