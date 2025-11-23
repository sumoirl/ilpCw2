package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DroneForServicePoint {

    @JsonProperty("servicePointId")
    private int servicePointId;

    @JsonProperty("drones")
    private List<DronesAvailibility> drones;

}
