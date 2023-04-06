import static java.lang.Math.sqrt;

public class Vector {
    public float x = 0;
    public float y = 0;
    public float z = 0;

    Vector(){}
    Vector(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    Vector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    Vector add(float x, float y){
        return new Vector(this.x + x, this.y + y);
    }

    Vector add(float x, float y, float z){
        return new Vector(this.x + x, this.y + y, this.z + z);
    }
    
    Vector sub(Vector other){
        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    Vector sub(float x, float y){
        return new Vector(this.x - x, this.y - y);
    }

    Vector sub(float x, float y, float z){
        return new Vector(this.x - x, this.y - y, this.z - z);
    }
    
    Vector mul(float scalar){
        return new Vector(this.x * scalar, this.y * scalar, this.z * scalar);
    }
    
    Vector div(float scalar){
        return mul(1/scalar);
    }
    
    float mag(){
        return (float)sqrt(x*x + y*y + z*z);
    }

    float dot(Vector other){
        return x * other.x + y * other.y + z * other.z;
    }

    Vector cross(Vector other){
        return new Vector(this.y*other.z - this.z*other.y, this.z*other.x - this.x*other.z, this.x*other.y - this.y*other.x);
    }

    Vector normalize(){
        float mag = this.mag();
        return new Vector(x / mag, y / mag, z / mag);
    }
}
