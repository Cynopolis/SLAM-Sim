package Vector;

import processing.core.PApplet;

public interface LineInterface {
    Vector getDirection();

    Vector getPosition();

    float getLength();

    float getAngle();

    Vector endPoint();

    float getDistance(Vector point);

    void draw(PApplet proc);

}
