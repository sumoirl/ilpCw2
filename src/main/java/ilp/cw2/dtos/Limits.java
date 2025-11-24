package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Limits {

    @JsonProperty("lower")
    private Integer lower;

    @JsonProperty("upper")
    private Integer upper;
}
