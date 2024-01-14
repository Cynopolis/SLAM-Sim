import Graph.*;
import Vector.Vector;
import processing.core.PApplet;

import java.io.FileNotFoundException;
import java.io.IOException;


public class Processing extends PApplet {

    Car car;
    public static PApplet processing;

    PointGraph map = new PointGraph();
    boolean mapIsHidden = false;
    boolean SLAMIsHidden = false;

    public static void main(String[] args) {
        PApplet.main("Processing");
    }

    public void settings(){
        processing = this;
        car = new Car(processing, 100,100,50,40);
        size(1000, 1000);
        car.addView(90,180);

//        for(int i = 0; i < 10; i++){
//            PointVertex vStart = new PointVertex(random(50, 950), random(50, 950));
//            PointVertex vEnd = new PointVertex(random(50, 950), random(50, 950));
//            map.addEdge(vStart, vEnd);
//        }

    }
    public void draw(){
        background(0);
        if(!mapIsHidden){
            map.draw(processing);
        }
        car.drawCar(map, SLAMIsHidden);
        strokeWeight(2);
        stroke(255);
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
        if(key == DELETE && map.vertexIsSelected()){
            map.removeVertex(map.getSelectedVertex());
        }
        if(key == ' ' && map.vertexIsSelected()){
            map.deselectVertex();
        }
        if(key == ESC){
            System.out.println("Attempting to save map to file.");
            try{
                PointGraphWriter writer = new PointGraphWriter();
                writer.save("map.txt", map);
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        if(key == 'l'){
            System.out.println("Attempting to load a map from file");
            PointGraphWriter writer = new PointGraphWriter();
            try {
                map = writer.loadFile("map.txt");
            } catch (NumberFormatException e) {
                System.out.println("Number format incorrect");
                e.printStackTrace();
            }
        }
        if(key == 'h'){
            mapIsHidden = !mapIsHidden;
        }
        if(key == 'j'){
            SLAMIsHidden = !SLAMIsHidden;
        }
    }

    public void mousePressed(){
        Vector clickPosition = new Vector(mouseX, mouseY);

        if(map.numVertices() == 0){
            PointVertex v = new PointVertex(clickPosition);
            map.addVertex(v);
            return;
        }
        PointVertex closestVertex = map.getClosestVertex(clickPosition);
        float distance = closestVertex.getPos().sub(clickPosition).mag();
        if(distance < 15){
            if(map.vertexIsSelected()){
                if(map.getSelectedVertex() == closestVertex){
                    map.deselectVertex();
                }
                else{
                    map.addEdge(map.getSelectedVertex(), closestVertex);
                }
                return;
            }
            map.setSelectedVertex(closestVertex);
            return;
        }
        PointVertex v = new PointVertex(clickPosition);
        map.addVertex(v);
    }
}