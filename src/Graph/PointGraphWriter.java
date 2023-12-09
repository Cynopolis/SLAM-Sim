package Graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class PointGraphWriter {
    public void save(String filename, PointGraph g) throws IOException {
        FileWriter file = new FileWriter(filename);

        file.write("numVerts," + g.numVertices());
        file.write("\nnumEdges," + g.numEdges());

        // turn the hash map into something linear
        ArrayList<PointVertex> verts = g.getAllVertexes();
        ArrayList<LineEdge> edges = g.getAllEdges();

        // save the vertexes
        int countVerts = 0;
        for(PointVertex v : verts){
            // save the vertex position
            file.write("\nvert,"+v.getPos().x+","+v.getPos().y);
            countVerts++;
        }

        // save the edges
        for(Edge e : edges){
            int idx = 0;
            file.write("\nedge,");
            boolean otherIsWritten = false;
            for(PointVertex v : verts){
                if(e.getStartVertex() == (Vertex)v){
                    file.write("start,"+ idx);
                    if(!otherIsWritten){
                        file.write(",");
                        otherIsWritten = true;
                    }
                }
                else if(e.getEndVertex() == (Vertex)v){
                    file.write("end," + idx);
                    if(!otherIsWritten){
                        file.write(",");
                        otherIsWritten = true;
                    }
                }
                idx++;
            }
        }
        file.close();
    }

    public PointGraph loadFile(String filename) throws NumberFormatException {
        PointGraph g = new PointGraph();
        File file = new File(filename);
        Scanner reader;
        try {
            reader = new Scanner(file);
        }
        catch (FileNotFoundException e){
            System.out.println("File not found");
            return g;
        }
        ArrayList<PointVertex> vertices = new ArrayList<>();
        while(reader.hasNextLine()){
            String line = reader.nextLine();
            ArrayList<String> args = parseLine(line);
            String key = args.get(0);
            switch (key) {
                case "numVerts" -> System.out.println("Number of Vertexes: " + Integer.parseInt(args.get(1)));
                case "numEdges" -> System.out.println("Number of Edges: " + Integer.parseInt(args.get(1)));
                case "vert" -> {
                    float x = Float.parseFloat(args.get(1));
                    float y = Float.parseFloat(args.get(2));
                    PointVertex v = new PointVertex(x, y);
                    g.addVertex(v);
                    vertices.add(v);
                }
                case "edge" -> {
                    int startIdx;
                    int endIdx;
                    if (args.get(1).contains("start")) {
                        startIdx = Integer.parseInt(args.get(2));
                        endIdx = Integer.parseInt(args.get(4));
                    }
                    else{
                        startIdx = Integer.parseInt(args.get(4));
                        endIdx = Integer.parseInt(args.get(2));
                    }
                    g.addEdge(vertices.get(startIdx), vertices.get(endIdx));
                }
                default -> System.out.println("Unrecognized Line: " + line);
            }
        }

        return g;
    }

    private ArrayList<String> parseLine(String line){
        ArrayList<String> args = new ArrayList<>();
        StringBuilder arg = new StringBuilder();

        for(char letter : line.toCharArray()){
            if(letter == ','){
                args.add(arg.toString());
                arg = new StringBuilder();
                continue;
            }
            if(letter == '\n'){
                args.add(arg.toString());
                break;
            }
            arg.append(letter);
        }
        if(!args.get(args.size()-1).contains(arg.toString())){
            args.add(arg.toString());
        }
        return args;
    }
}
