package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Capabilities {
    @JsonProperty("cooling")
    private boolean cooling;
    @JsonProperty("heating")
    private boolean heating;
    @JsonProperty("capacity")
    private double capacity;
    @JsonProperty("maxMoves")
    private int maxMoves;
    @JsonProperty("costPerMove")
    private double costPerMove;
    @JsonProperty("costInitial")
    private double costInitial;
    @JsonProperty("costFinal")
    private double costFinal;
}
