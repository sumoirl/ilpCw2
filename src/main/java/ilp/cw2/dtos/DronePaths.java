package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DronePaths {
    @JsonProperty(value = "droneId")
    public String droneId;

    @JsonProperty(value = "deliveries")
    public List<DronePath> deliveries;
}
