package Vector;

import static java.lang.Math.*;

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

    public float angle(){
        return (float) atan2(y, x);
    }
}
