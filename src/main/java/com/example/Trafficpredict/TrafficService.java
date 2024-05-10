package com.example.Trafficpredict;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TrafficService {

    @Autowired
    private ItApiProperties itApiProperties;

    private static final String DATABASE_URL = "jdbc:sqlite:src/main/resources/daejeon_links_without_geometry.sqlite";
    private static final int EXCLUDE_CITY_LEVEL = 5; // 상수

    public TrafficResponse callApi(TrafficRequest request) throws IOException, SQLException {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(itApiProperties.getApiUrl()).newBuilder();
        urlBuilder.addQueryParameter("apiKey", itApiProperties.getApiKey())
                .addQueryParameter("type", "all")
                .addQueryParameter("drcType", "all")
                .addQueryParameter("minX", request.getMinX())
                .addQueryParameter("maxX", request.getMaxX())
                .addQueryParameter("minY", request.getMinY())
                .addQueryParameter("maxY", request.getMaxY())
                .addQueryParameter("getType", "json");

        Request httpRequest = new Request.Builder()
                .url(urlBuilder.build())
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBody = response.body().string();
            return convertData(new JSONObject(responseBody), request.getMapLevel());
        }
    }

    private TrafficResponse convertData(JSONObject apiResponse, int mapLevel) throws SQLException {
        JSONArray items = apiResponse.getJSONObject("body").getJSONArray("items");
        List<TrafficData> dataList = new ArrayList<>();
        int currentId = 1;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT GEOMETRY, link_id, road_name, road_rank FROM daejeon_link WHERE link_id = ?");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                int linkId;
                try {
                    linkId = item.getInt("linkId");
                } catch (JSONException e) {
                    try {
                        linkId = Integer.parseInt(item.getString("linkId"));
                    } catch (NumberFormatException ex) {
                        log.error("Invalid format for linkId", ex);
                        continue;
                    }
                }

                stmt.setInt(1, linkId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next() && !(rs.getString("road_rank").matches("105|106|107")) &&
                        !(mapLevel >= EXCLUDE_CITY_LEVEL && rs.getString("road_rank").equals("104"))) {

                    TrafficData data = new TrafficData();
                    data.setId(currentId++);
                    data.setLinkId(linkId);
                    data.setNodeId(item.optInt("startNodeId"));
                    data.setRoadName(rs.getString("road_name"));
                    data.setRoadRank(rs.getString("road_rank"));
                    data.setGeometry(rs.getString("GEOMETRY"));
                    data.setSpeed(item.getDouble("speed"));
                    data.setRoadStatus(determineCongestion(rs.getString("road_rank"), item.getDouble("speed")));
                    data.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+09:00'")));
                    dataList.add(data);
                }
            }
        }
        TrafficResponse trafficResponse = new TrafficResponse();
        trafficResponse.setItems(dataList);
        return trafficResponse;
    }


    private int determineCongestion(String roadRank, double speed) {
        switch (roadRank) {
            case "101": return speed <= 40 ? 3 : speed <= 80 ? 2 : 1;
            case "102": return speed <= 30 ? 3 : speed <= 60 ? 2 : 1;
            case "103": return speed <= 20 ? 3 : speed <= 40 ? 2 : 1;
            case "104": return speed <= 15 ? 3 : speed <= 30 ? 2 : 1;
            default: return 0;  // unknown
        }
    }
}
