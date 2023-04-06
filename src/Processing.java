import processing.core.PApplet;

public class Processing extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Processing");
    }

    public void settings(){
        size(200, 200);
    }
    public void draw(){
        background(0);
        ellipse(mouseX, mouseY, 20, 20);
    }


}