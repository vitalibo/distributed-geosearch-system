package com.github.vitalibo.geosearch.subject.infrastructure.websocket.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.List;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class BlitzortungLightningStrike {

    @JsonProperty(value = "time")
    private Long timestampInNanoSecond;

    @JsonProperty(value = "lat")
    private Double latitudeInDegree;

    @JsonProperty(value = "lon")
    private Double longitudeInDegree;

    @JsonProperty(value = "alt")
    private Integer altitudeInMeter;

    @JsonProperty(value = "pol")
    private Byte polarity;

    @JsonProperty(value = "mds")
    private Long maximalDeviationSpanInNanoSecond;

    @JsonProperty(value = "mcg")
    private Integer maximalCircularGapInDegree;

    @JsonProperty(value = "status")
    private Integer status;

    @JsonProperty(value = "region")
    private Integer region;

    @JsonProperty(value = "sig")
    private List<BlitzortungSignal> signals;

    @JsonProperty(value = "delay")
    private Double delay;

}
