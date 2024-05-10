package com.example.Trafficpredict;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class TrafficData {
    @JsonProperty("id")
    private int id;

    @JsonProperty("geometry")
    private String geometry;

    @JsonProperty("speed")
    private double speed;

    @JsonProperty("road_status")
    private int roadStatus;

    @JsonProperty("date")
    private String date;

    @JsonProperty("link_Id")
    private long linkId;

    @JsonProperty("Node_Id")
    private long nodeId;

    @JsonProperty("road_name")
    private String roadName;

    @JsonProperty("road_rank")
    private String roadRank;

    public void setDateFromDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+09:00'");
        this.date = dateTime.format(formatter);
    }
}
