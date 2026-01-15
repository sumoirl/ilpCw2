package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Requirements {
    @JsonProperty("capacity")
    private Double capacity;

    @JsonProperty("cooling")
    private Boolean cooling;

    @JsonProperty("heating")
    private Boolean heating;

    @JsonProperty("maxCost")
    private Double maxCost;

    public boolean maxCostNull() {
        return maxCost == null;
    }
}
