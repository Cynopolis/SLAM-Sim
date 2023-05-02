package Graph;

public class Edge {
    Vertex vStart = null;
    Vertex vEnd = null;
    float weight = 0;

    /**
     * @param vStart the vertex the edge starts at
     * @param vEnd the vertex the edge ends at
     * @param weight the weight of the edge
     */
    Edge(Vertex vStart, Vertex vEnd, float weight){
        this.vStart = vStart;
        this.vEnd = vEnd;
        this.weight = weight;
    }

    /**
     * @param vStart the vertex the edge starts at
     * @param vEnd the vertex the edge ends at
     */
    Edge(Vertex vStart, Vertex vEnd){
        this.vStart = vStart;
        this.vEnd = vEnd;
    }

    Edge(){}

    /**
     * @return the weight of the edge
     */
    public float getWeight(){
        return weight;
    }

    /**
     * @param newWeight set the edge's weight to something new
     */
    public void setWeight(float newWeight){
        this.weight = newWeight;
    }

    /**
     * @return the vertex at the end of the edge
     */
    public Vertex getEndVertex(){
        return vEnd;
    }

    /**
     * @return the vertex at the start of the edge
     */
    public Vertex getStartVertex(){
        return vStart;
    }

}

