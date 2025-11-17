package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Region {
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "vertices")
    private ArrayList<Point> vertices;

    public Region(String name, ArrayList<Point> vertices) {
        this.name = name;
        this.vertices = vertices;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ArrayList<Point> getVertices() {
        return vertices;
    }
    public void setVertices(ArrayList<Point> vertices) {
        this.vertices = vertices;
    }

    public boolean isValid(){
        for (Point p : vertices){
            if (!p.isValid()){
                return false;
            }
        }
        return true;
    }


}
