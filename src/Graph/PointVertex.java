package Graph;
import Vector.Vector;
import processing.core.PApplet;

public class PointVertex extends Vertex {
    private Vector position;
    private int[] color = new int[]{127, 255, 0, 0};

    /**
     * @param xPos the x position of the vertex
     * @param yPos the y posiiton of the vertex
     */
    public PointVertex(float xPos, float yPos){
        this.position = new Vector(xPos, yPos);
    }

    public PointVertex(Vector position){
        super();
        this.position = position;
    }

    /**
     * @param xPos the x position of the vertex
     * @param yPos the y posiiton of the vertex
     * @param label the label of the vertex
     */
    PointVertex(float xPos, float yPos, String label){
        super(label);
        this.position = new Vector(xPos, yPos);
    }

    /**
     * @param x the new x position of the vertex
     * @param y the new y posiiton of the vertex
     */
    public void setPos(float x, float y){
        this.position = new Vector(x, y);
    }

    /**
     * @return a two eleement float array containing the x and y coordinates of the vertex respectively.
     */
    public Vector getPos(){
        return position;
    }

    /**
     * @param newColor a 4 element int array containing th alpha, r, g ,and b components respectively
     */
    public void setColor(int[] newColor){
        this.color = newColor;
    }

    /**
     * @return a 4 element int array containing th alpha, r, g ,and b components respectively
     */
    public int[] getColor(){
        return color;
    }

    public void draw(PApplet proc){
        proc.stroke(color[1], color[2], color[3], color[0]);
        proc.circle(position.x, position.y, 20);
    }
}