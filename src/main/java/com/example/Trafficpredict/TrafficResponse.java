package com.example.Trafficpredict;

import lombok.Data;
import java.util.List;

@Data
public class TrafficResponse {
    private List<TrafficData> items;
}
