package ilp.cw2.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class MedDispatchRec {

    @JsonProperty("id")
    private int id;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("time")
    private LocalTime time;

    @JsonProperty("requirements")
    private Requirements requirements;

    @JsonProperty("delivery")
    private Point delivery;


}
