package com.example.Trafficpredict;

import lombok.Data;

@Data
public class TrafficRequest {
    private String minX;
    private String maxX;
    private String minY;
    private String maxY;
    private int mapLevel;
}
