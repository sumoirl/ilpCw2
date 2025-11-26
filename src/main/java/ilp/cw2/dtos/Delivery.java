package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Delivery {

    @JsonProperty("deliveryId")
    public String deliveryId;

    @JsonProperty("flightPath")
    public List<Point> flightPath;
}
