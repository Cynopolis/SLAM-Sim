import Vector.*;
import processing.core.*;

import static processing.core.PApplet.*;

public class Wall extends Line {
    int r;
    int g;
    int b;

    private final PApplet proc;

    Wall(PApplet proc, Vector pos, float angle, int wallLength){
        super(pos, (new Vector(cos(angle), sin(angle)).mul(wallLength)).add(pos));
        this.proc = proc;
        r = (int)proc.random(50, 255);
        g = (int)proc.random(50, 255);
        b = (int)proc.random(50, 255);
    }

    Wall(PApplet proc, Vector startPos, Vector endPos){
        super(startPos, endPos);
        this.proc = proc;
        r = (int)proc.random(50, 255);
        g = (int)proc.random(50, 255);
        b = (int)proc.random(50, 255);
    }

    void drawWall(){
        proc.stroke(r,g,b);
        proc.strokeWeight(10);
        proc.circle(position.x, position.y, 10);
        proc.strokeWeight(2);
        proc.line(position.x, position.y, position.x + direction.x, position.y + direction.y);
    }

    Vector getPos(){
        return position;
    }
}