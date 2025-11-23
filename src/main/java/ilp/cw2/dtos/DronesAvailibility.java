package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DronesAvailibility {

    @JsonProperty("id")
    public String id;

    @JsonProperty("availability")
    public List<Availibility> availability;
}
