package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LngLatAlt {

    @JsonProperty("lng")
    private Double lng;

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("alt")
    private Double alt;
}
