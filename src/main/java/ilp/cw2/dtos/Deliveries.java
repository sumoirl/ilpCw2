package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Deliveries {

    @JsonProperty("deliveryId")
    public String deliveryId;

    @JsonProperty("flightPath")
    public List<Point> flightPath;
}
