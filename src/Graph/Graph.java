package Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Graph {

    /**
     * Create a new empty graph
     */
    public Graph(){
        // hash maps require an initial size to get started
        adjList = new HashMap<Vertex, LinkedList<Edge>>(1024);
    }

    /**
     * @return the total number of vertices in the graph
     */
    public int numVertices(){
        return adjList.size();
    }

    /**
     * @return the total number of edges in the graph.
     */
    public int numEdges(){
        int sum = 0;
        for(Vertex v : adjList.keySet()){
            sum += adjList.get(v).size();
        }
        return sum;
    }

    /**
     * @param v add the vertex v to the graph
     */
    public void addVertex(Vertex v){
        // check if it's already in the adjacency listVertex
        if(adjList.containsKey(v)){
            return;
        }
        adjList.put(v, new LinkedList<Edge>());

    }

    /**
     * @param vStart the starting vertex
     * @param vEnd the ending vertex
     */
    public void addEdge(Vertex vStart, Vertex vEnd){
        // don't add the edge if it is already added
        for(Edge e : adjList.get(vStart)){
            if(e.getEndVertex() == vEnd){
                return;
            }
        }
        adjList.get(vStart).add(new Edge(vStart, vEnd));
    }

    /**
     * @brief Gets how many other vertices a given vertex points to
     * @param v the vertex you want to know about
     * @return the number of vertices this vertex points to
     */
    public int outDegree(Vertex v){
        return adjList.get(v).size();
    }

    /**
     * @param v the vertex of interest
     * @return the number of edges going into the vertex
     */
    public int inDegree(Vertex v){
        int count = 0;
        for(Vertex v1 : adjList.keySet()){
            for(Edge edge : adjList.get(v1)){
                if(edge.getEndVertex() == v){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Print out all vertexes in the graph
     */
    public void print(){
        for(Vertex key : adjList.keySet()){
            System.out.print("Key: " + key.getLabel() + ": ");
            for(Edge edge : adjList.get(key)){
                System.out.print(edge.getEndVertex().getLabel());
                System.out.print(",");
            }
            System.out.println("NULL");
        }
    }

    /**
     * @param v the vertext to start the traverse at
     * @return a list of vertexes in order of when they were visited from first to last.
     */
    public ArrayList<Vertex> depthFirstTraverse(Vertex v){
        ArrayList<Vertex> vertexList = new ArrayList<Vertex>();
        DFSTraverse(v, vertexList);
        resetVisits();

        return vertexList;
    }

    /**
     * @param v the vertex to begin the traverse at
     * @param vertexList the vertex list to append visited vertexes to
     */
    private void DFSTraverse(Vertex v, ArrayList<Vertex> vertexList){
        vertexList.add(v);
        v.setVisitedStatus(true);
        LinkedList<Edge> edgeList = adjList.get(v);
        for(Edge edge : edgeList){
            if(!(edge.getEndVertex().visitedStatus())){
                DFSTraverse(edge.getEndVertex(), vertexList);
            }
        }
    }

    /**
     * @post all vertexes in the graph will be set to unvisited
     */
    private void resetVisits(){
        for(Vertex v : adjList.keySet()){
            v.setVisitedStatus(false);
        }
    }

    /**
     * @param v the vertex that you wish to see the neighbors of
     * @return a list of vertexes that are connected to v with ingoing or outgoing edges
     */
    public ArrayList<Vertex> getNeightborVerts(Vertex v){
        // iterate through all of the vertexes and make sure they're marked as unvisited
        for(Vertex v1 : adjList.keySet()){
            v1.setVisitedStatus(false);
        }

        ArrayList<Vertex> neighbors = new ArrayList<>();
        for(Vertex v1 : adjList.keySet()){
            for(Edge edge : adjList.get(v1)){
                if(edge.getStartVertex() == v && !edge.getEndVertex().visitedStatus()){
                    edge.getEndVertex().setVisitedStatus(true);
                    neighbors.add(edge.getEndVertex());
                }

                if(edge.getEndVertex() == v && !edge.getStartVertex().visitedStatus()){
                    edge.getStartVertex().setVisitedStatus(true);
                    neighbors.add(edge.getStartVertex());
                }
            }
        }

        resetVisits();

        return neighbors;
    }

    /**
     * @param v the vertex that you wish to start the search at
     * @return A list of vertexes in order of visitation where the search goes wide and then deep
     */
    public ArrayList<Vertex> breadthFirstSearch(Vertex v){
        ArrayList<Vertex> queue = new ArrayList<>();
        ArrayList<Vertex> outputList = new ArrayList<>();
        outputList.add(v);
        queue.add(v);
        v.setVisitedStatus(true);

        while(queue.size() > 0){
            Vertex next = queue.get(0);
            queue.remove(0);
            LinkedList<Edge> edgeList = adjList.get(next);
            for(Edge edge : edgeList){
                if(!edge.getEndVertex().visitedStatus()){
                    queue.add(edge.getEndVertex());
                    outputList.add(edge.getEndVertex());
                    edge.getEndVertex().setVisitedStatus(true);
                }
            }
        }

        resetVisits();

        return outputList;
    }

    /**
     * @brief remove the given vertex and all edges that reference it from the graph
     * @param v the vertex to remove from the graph.
     */
    public void removeVertex(Vertex v){

        // find all edges that point to the removed vertex
        ArrayList<Edge> edgesToRemove = new ArrayList<>();
        ArrayList<Vertex> startVertex = new ArrayList<>();
        for(Vertex v1 : adjList.keySet()){
            for(Edge e : adjList.get(v1)){
                if(e.getEndVertex() == v){
                    edgesToRemove.add(e);
                    startVertex.add(e.getStartVertex());
                }
            }
        }

        // remove all of those edges from the adjList
        int i = 0;
        for(Edge e : edgesToRemove){
            adjList.get(startVertex.get(i)).remove(e);
            i++;
        }
        // remove the vertex from the adjacency list
        adjList.remove(v);
    }

    protected HashMap<Vertex, LinkedList<Edge>> adjList;
}

