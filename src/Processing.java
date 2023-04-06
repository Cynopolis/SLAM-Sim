import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Processing extends PApplet {

    Car car;
    ArrayList<Wall> objects = new ArrayList<>();

    public static PApplet processing;

    public static void main(String[] args) {
        PApplet.main("Processing");
    }

    public void settings(){
        processing = this;
        car = new Car(processing, 100,100,50,40);
        size(1000, 1000);
        car.addView(60,6);
        for(int i = 0; i < 20; i++){
            Wall wall = new Wall(processing, new PVector((int)random(40, 1840), (int)random(40, 960)), (int)random(360), (int)random(100, 1000));
            objects.add(wall);
        }
    }
    public void draw(){
        background(0);
        for(Wall object : objects){
            object.drawWall();
        }
        car.drawCar(objects);
        //car.drive(new int[] {0, 0});
    }

    public void keyPressed(){
        if(key == 'd'){
            car.setPose(car.getPose().add(1, 0));
        }
        if(key == 'w'){
            car.setPose(car.getPose().add(0, -1));
        }
        if(key == 'a'){
            car.setPose(car.getPose().add(-1, 0));
        }
        if(key == 's'){
            car.setPose(car.getPose().add(0, 1));
        }
        if(key == 'q'){
            car.setAngle(car.getAngle()+1);
        }
        if(key == 'e'){
            car.setAngle(car.getAngle()-1);
        }

    }



}