package com.github.vitalibo.geosearch.subject.infrastructure.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class BlitzortungSignal {

    @JsonProperty(value = "sta")
    private Integer station;

    @JsonProperty(value = "time")
    private Long timestampDifferenceInNanoSecond;

    @JsonProperty(value = "lat")
    private Double latitudeInDegree;

    @JsonProperty(value = "lon")
    private Double longitudeInDegree;

    @JsonProperty(value = "alt")
    private Integer altitudeInMeter;

    /**
     * bit 1 = polarity negative
     * bit 2 = polarity positive
     * bit 3 = signal is used for the computation
     */
    @JsonProperty(value = "status")
    private Byte status;

}
