package com.example.Trafficpredict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TrafficController {

    @Autowired
    private TrafficService trafficService;

    @PostMapping("/main")
    public ResponseEntity<?> getTrafficData(@RequestBody TrafficRequest request) {
        try {
            TrafficResponse data = trafficService.callApi(request);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("API 요청 실패: " + e.getMessage());
        }
    }
}
