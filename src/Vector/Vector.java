package Vector;

import org.ejml.simple.SimpleMatrix;
import processing.core.PApplet;

import static java.lang.Math.*;
import static processing.core.PApplet.cos;
import static processing.core.PApplet.sin;

public class Vector {
    public float x = 0;
    public float y = 0;
    public float z = 0;

    Vector(){}
    public Vector(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    public Vector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(SimpleMatrix matrix){
        // initialize x,y if matrix is 2x1 and x,y,z if matrix is 3x1
        if(matrix.getNumRows() == 2){
            this.x = (float)matrix.get(0,0);
            this.y = (float)matrix.get(1,0);
        }else if(matrix.getNumRows() == 3){
            this.x = (float)matrix.get(0,0);
            this.y = (float)matrix.get(1,0);
            this.z = (float)matrix.get(2,0);
        }
    }
    
    public Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public Vector add(float x, float y){
        return new Vector(this.x + x, this.y + y);
    }

    public Vector add(float x, float y, float z){
        return new Vector(this.x + x, this.y + y, this.z + z);
    }
    
    public Vector sub(Vector other){
        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public Vector sub(float x, float y){
        return new Vector(this.x - x, this.y - y);
    }

    public Vector sub(float x, float y, float z){
        return new Vector(this.x - x, this.y - y, this.z - z);
    }
    
    public Vector mul(float scalar){
        return new Vector(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    
    public Vector div(float scalar){
        return mul(1/scalar);
    }
    
    public float mag(){
        return (float)sqrt(x*x + y*y + z*z);
    }

    public float dot(Vector other){
        return x * other.x + y * other.y + z * other.z;
    }

    public Vector cross(Vector other){
        return new Vector(this.y*other.z - this.z*other.y, this.z*other.x - this.x*other.z, this.x*other.y - this.y*other.x);
    }

    public Vector normalize(){
        float mag = this.mag();
        return new Vector(x / mag, y / mag, z / mag);
    }

    /**
     * @param other
     * @return
     */
    public float angleDiff(Vector other){
        float dot = this.dot(other);     // dot product
        float det = this.x*other.y - this.y*other.x;    // determinant
        float angle = (float) atan2(det, dot);  // atan2(y, x) or atan2(sin, cos)
        return angle;
    }

    /**
     * @return The angle of the vector in radians
     */
    public float angle(){
        return (float) atan2(y, x);
    }

    /**
     * @brief Rotate a 2D vector by a given angle
     * @param angle The angle to rotate the vector by in radians
     * @return The rotated vector
     */
    public Vector rotate2D(float angle){
        float distance = mag();
        float currentAngle = this.angle();
        return new Vector(cos(currentAngle + angle), sin(currentAngle + angle)).mul(distance);
    }

    public void draw(PApplet proc){
        proc.circle(this.x, this.y, 8);
    }

    public float[] toArray() {
        return new float[]{x, y};
    }
}
