package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DroneForServicePoint {

    @JsonProperty("servicePointId")
    public int servicePointId;

    @JsonProperty("drones")
    public List<DronesAvailibility> drones;

}
