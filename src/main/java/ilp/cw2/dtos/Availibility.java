package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;


@Data
public class Availibility {

    @JsonProperty("dayOfWeek")
    private DayOfWeek dayOfWeek;
    @JsonProperty("from")
    private LocalTime from;
    @JsonProperty("until")
    private LocalTime until;
}
