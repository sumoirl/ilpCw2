package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import ilp.cw2.utils.Raycasting;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestrictedArea {

    @JsonProperty("name")
    private String name;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("limits")
    private Limits limits;

    @JsonProperty("vertices")
    private ArrayList<LngLatAlt> vertices;

    public List<Raycasting.Line> areaToLines() {
        List<Raycasting.Line> lines = new ArrayList<>();

        for(int i = 0; i < this.vertices.size() - 1; i++) {
            Point p1 = new Point(vertices.get(i).getLng(), vertices.get(i).getLng());
            Point p2 = new Point(vertices.get(i + 1).getLng(), vertices.get(i + 1).getLng());
            lines.add(new Raycasting.Line(p1, p2));
        }

        return lines;
    }

}
