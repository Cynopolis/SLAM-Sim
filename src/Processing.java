import Graph.*;
import processing.core.PApplet;


public class Processing extends PApplet {

    Car car;
    public static PApplet processing;

    PointGraph map = new PointGraph();

    public static void main(String[] args) {
        PApplet.main("Processing");
    }

    public void settings(){
        processing = this;
        car = new Car(processing, 100,100,50,40);
        size(1000, 1000);
        car.addView(360,180);

        for(int i = 0; i < 10; i++){
            PointVertex vStart = new PointVertex(random(50, 950), random(50, 950));
            PointVertex vEnd = new PointVertex(random(50, 950), random(50, 950));
            map.addEdge(vStart, vEnd);
        }

    }
    public void draw(){
        background(0);
        map.draw(processing);
        car.drawCar(map);
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