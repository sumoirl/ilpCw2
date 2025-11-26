package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DeliveryOutput {
    @JsonProperty(value = "totalCost")
    public Double totalCost;

    @JsonProperty(value = "totalMoves")
    public Integer totalMoves;

    @JsonProperty(value = "dronePaths")
    public List<DronePath> dronePaths;
}
