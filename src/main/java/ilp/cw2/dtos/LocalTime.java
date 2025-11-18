package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LocalTime {

    @JsonProperty("hour")
    public int hour;
    @JsonProperty("minute")
    public int minute;
    @JsonProperty("second")
    public int second;
    @JsonProperty("nano")
    public int nano;
}
