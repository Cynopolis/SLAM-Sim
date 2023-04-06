import processing.core.*;

import static processing.core.PApplet.*;

public class Wall{
    PVector pos;
    float angle;
    int wallLength;
    private static PApplet proc;
    int r;
    int g;
    int b;

    Wall(PApplet processing, PVector pos, float angle, int wallLength){
        proc = processing;
        this.pos = pos;
        this.angle = angle;
        this.wallLength = wallLength;
        r = (int)proc.random(50, 255);
        g = (int)proc.random(50, 255);
        b = (int)proc.random(50, 255);
    }

    void drawWall(){
        proc.stroke(r,g,b);
        proc.line(pos.x, pos.y, (pos.x + cos(radians(angle))*wallLength), (pos.y + sin(radians(angle))*wallLength));
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