import Vector.Vector;
import processing.core.PApplet;

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
        car.addView(360,180);
        for(int i = 0; i < 15; i++){
            Wall wall = new Wall(processing, new Vector((int)random(50, 950), (int)random(50, 950)), new Vector((int)random(50, 950), (int)random(50, 950)));
            objects.add(wall);
        }
    }
    public void draw(){
        background(0);
//        for(Wall object : objects){
//            object.drawWall();
//        }
        car.drawCar(objects);
        View view = car.views.get(0);
        view.look(objects);
        ArrayList<Vector> points = view.getPoints();
        for(Vector point: points){
            circle(point.x, point.y, 5);
        }
        strokeWeight(2);
        stroke(255);
        //car.drive(new int[] {0, 0});
    }

    public void keyPressed(){
        if(key == 'd'){
            car.setPose(car.getPose().add(10, 0));
        }
        if(key == 'w'){
            car.setPose(car.getPose().add(0, -10));
        }
        if(key == 'a'){
            car.setPose(car.getPose().add(-10, 0));
        }
        if(key == 's'){
            car.setPose(car.getPose().add(0, 10));
        }
        if(key == 'q'){
            car.setAngle(car.getAngle()+1);
        }
        if(key == 'e'){
            car.setAngle(car.getAngle()-1);
        }

    }



}