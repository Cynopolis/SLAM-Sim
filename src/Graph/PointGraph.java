package Graph;

import Vector.Vector;
import processing.core.PApplet;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

import java.util.ArrayList;


public class PointGraph extends Graph {
    PointVertex selectedVertex;
    PointGraph(){
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
    public PointVertex getClosestVertex(float x, float y){
        if(super.adjList.size() == 0){
            // TODO: choose a better exception name
            throw new NullPointerException();
        }

        PointVertex closestVertex = (PointVertex) super.adjList.keySet().iterator().next();
        float closestDist = -1;
        for(Vertex v : super.adjList.keySet()){
            PointVertex v1 = (PointVertex) v;
            Vector p2 = v1.getPos();
            float dist = (float)sqrt(pow(x-p2.x, 2) + pow(y-p2.y, 2));
            if(dist < closestDist || closestDist == -1){
                closestDist = dist;
                closestVertex = v1;
            }
        }

        return closestVertex;
    }

    /**
     * @return a bundle with all of the graphs vertex and edge information saved into it
     */
    /*
    public Bundle saveToBundle(){
        Bundle bundle = new Bundle();
        bundle.putInt("numVerts", super.numVertices());
        bundle.putInt("numEdges", super.numEdges());

        // turn the hash map into something linear
        ArrayList<PointVertex> verts = new ArrayList<>();
        ArrayList<Edge> edges = new ArrayList<>();
        for(Vertex v : super.adjList.keySet()){
            verts.add((PointVertex) v);
            for(Edge e : super.adjList.get(v)){
                edges.add(e);
            }
        }

        // save the vertexes
        int countVerts = 0;
        for(PointVertex v : verts){
            String countVertsString = "vert"+String.valueOf(countVerts);
            // save the vertex position
            bundle.putFloatArray(countVertsString+"Pos", v.getPos());
            // save the vertex label
            bundle.putString(countVertsString+"Label", v.getLabel());

            // save if the vertex is selected
            if(selectedVertex != null){
                // save if it is the selected vertex
                bundle.putBoolean(countVertsString+"isSelected", v == selectedVertex);
            }
            else{
                bundle.putBoolean(countVertsString+"isSelected", false);
            }
            countVerts++;
        }

        // save the edges
        int countEdges = 0;
        for(Edge e : edges){
            int idx = 0;
            String countEdgesString = "edge" + String.valueOf(countEdges);
            for(PointVertex v : verts){
                if(e.getStartVertex() == (Vertex)v){
                    bundle.putInt(countEdgesString+"Start", idx);
                }
                else if(e.getEndVertex() == (Vertex)v){
                    bundle.putInt(countEdgesString+"End", idx);
                }
                idx++;
            }
            countEdges++;
        }
        return bundle;
    }
    */
    /**
     * @ brief add all graph information in ta bundle to the graph
     * @param bundle the bundle to add to the graph
     */
    /*
    public void addBundleToGraph(Bundle bundle){
        int numVerts = bundle.getInt("numVerts", 0);
        int numEdges = bundle.getInt("numEdges", 0);
        ArrayList<PointVertex> verts = new ArrayList<>();

        // add all of the vertexes from the bundle
        for(int i = 0; i < numVerts; i++){
            String countVertsString = "vert"+String.valueOf(i);
            float[] pos = bundle.getFloatArray(countVertsString+"Pos");
            String label = bundle.getString(countVertsString+"Label");
            boolean isSelected = bundle.getBoolean(countVertsString+"isSelected");
            PointVertex v = new PointVertex(pos[0], pos[1], label);
            addVertex(v);
            verts.add(v);
            if(isSelected){
                setSelectedVertex(v);
            }
        }

        // add all of the edges from the bundle
        for(int i = 0; i < numEdges; i++){
            String countEdgesString = "edge" + String.valueOf(i);
            int startVertIndex = bundle.getInt(countEdgesString+"Start");
            int endVertIndex = bundle.getInt(countEdgesString+"End");
            PointVertex vStart = verts.get(startVertIndex);
            PointVertex vEnd = verts.get(endVertIndex);
            addEdge(vStart, vEnd);
        }
    }
    */

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
