package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Availibility {

    @JsonProperty("dayOfWeek")
    public String dayOfWeek;
    @JsonProperty("from")
    public LocalTime from;
    @JsonProperty("until")
    public LocalTime until;
}
