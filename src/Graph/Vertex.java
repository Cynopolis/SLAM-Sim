package Graph;

public class Vertex {
    private String vertexLabel;
    private boolean isVisitedStatus = false;

    /**
     * Create a new vertex with a default label
     */
    public Vertex(){
        this.vertexLabel = "Unassigned";
    }

    /**
     * @param label create a new vertex with this label
     */
    public Vertex(String label){
        this.vertexLabel = label;
    }

    /**
     * @return the label of the vertex
     */
    public String getLabel(){
        return vertexLabel;
    }

    public void setLabel(String newLabel){
        vertexLabel = newLabel;
    }

    /**
     * @return if the node has been visited or not
     */
    public boolean visitedStatus(){
        return this.isVisitedStatus;
    }

    /**
     * @param status set the visited status to true or false
     */
    public void setVisitedStatus(boolean status){
        this.isVisitedStatus = status;
    }
}