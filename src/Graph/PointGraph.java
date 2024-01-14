package Graph;

import Vector.Vector;
import processing.core.PApplet;

import java.io.File;  // Import the File class
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;


public class PointGraph extends Graph {
    PointVertex selectedVertex;
    public PointGraph(){
        super();
    }

    public void draw(PApplet proc){
        for(Vertex v : adjList.keySet()){
            PointVertex v1 = (PointVertex) v;
            v1.draw(proc);
            for(Edge e : adjList.get(v)){
                LineEdge e1 = (LineEdge) e;
                e1.draw(proc);
            }
        }
    }

    public void addEdge(PointVertex vStart, PointVertex vEnd){
        addVertex(vStart);

        // don't add the edge if it is already added
        for(Edge e : adjList.get(vStart)){
            if(e.getEndVertex() == (Vertex) vEnd){
                return;
            }
        }

        addVertex(vEnd);
        LineEdge edge = new LineEdge(vStart, vEnd);
        adjList.get((Vertex) vStart).add(new LineEdge(vStart, vEnd));
    }

    /**
     * @param v set this vertex as the selected vertex in the graph
     */
    public void setSelectedVertex(PointVertex v){
        if(selectedVertex != null){
            deselectVertex();
        }
        selectedVertex = v;
        selectedVertex.setColor(new int[]{255, 0, 255, 0});
    }

    /**
     * @pre a vertex needs to be selected
     * @return the current pointvertex which is selected in the graph
     */
    public PointVertex getSelectedVertex(){
        if(selectedVertex == null){
            if(super.adjList.size() > 0){
                selectedVertex = (PointVertex)super.adjList.keySet().iterator().next();
            }
            else{
                throw new NullPointerException();
            }
        }
        return selectedVertex;
    }

    /**
     * @return true if a vertex is currently selected in the graph
     */
    public boolean vertexIsSelected(){
        return selectedVertex != null;
    }

    /**
     * @brief deselect the currently selected vertex
     */
    public void deselectVertex(){
        if(selectedVertex == null){
            return;
        }
        selectedVertex.setColor(new int[]{127, 255, 0, 0});
        selectedVertex = null;
    }

    /**
     * @param x the x coordinate to search by
     * @param y the y coordinate to search by
     * @return the pointvertex closest to the given x, y coordinates
     */
    public PointVertex getClosestVertex(Vector point){
        if(super.adjList.size() == 0){
            // TODO: choose a better exception name
            throw new NullPointerException();
        }

        PointVertex closestVertex = (PointVertex) super.adjList.keySet().iterator().next();
        float closestDist = -1;
        for(Vertex v : super.adjList.keySet()){
            PointVertex v1 = (PointVertex) v;
            Vector p2 = v1.getPos();
            float dist = p2.sub(point).mag();
            if(dist < closestDist || closestDist == -1){
                closestDist = dist;
                closestVertex = v1;
            }
        }

        return closestVertex;
    }

    public void removeVertex(PointVertex v){
        if(selectedVertex == v){
            selectedVertex = null;
        }
        super.removeVertex(v);
    }


    /**
     * @return all edges in the graph
     */
    public ArrayList<LineEdge> getAllEdges(){
        ArrayList<LineEdge> edges = new ArrayList<>();
        for(Vertex v : adjList.keySet()){
            for(Edge e : adjList.get(v)){
                LineEdge e1 = (LineEdge) e;
                edges.add(e1);
            }
        }
        return edges;
    }

    /**
     * @return all vertexes in the graph
     */
    public ArrayList<PointVertex> getAllVertexes(){
        ArrayList<PointVertex> points = new ArrayList<>();
        for(Vertex v : adjList.keySet()){
            PointVertex v1 = (PointVertex) v;
            points.add(v1);
        }
        return points;
    }
}
