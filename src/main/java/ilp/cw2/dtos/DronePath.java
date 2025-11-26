package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DronePath {
    @JsonProperty(value = "droneId")
    public String droneId;

    @JsonProperty(value = "deliveries")
    public List<Delivery> deliveries;
}
