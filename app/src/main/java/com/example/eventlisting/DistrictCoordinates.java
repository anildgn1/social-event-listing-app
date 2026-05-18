package com.example.eventlisting;

import com.google.android.gms.maps.model.LatLng;
import java.util.HashMap;
import java.util.Map;

public class DistrictCoordinates {
    private static final Map<String, LatLng> districtCoordinates = new HashMap<>();

    static {
        districtCoordinates.put("Adalar", new LatLng(40.8742, 29.1259));
        districtCoordinates.put("Arnavutköy", new LatLng(41.2019, 28.7403));
        districtCoordinates.put("Ataşehir", new LatLng(40.9923, 29.1278));
        districtCoordinates.put("Avcılar", new LatLng(40.9797, 28.7214));
        districtCoordinates.put("Bağcılar", new LatLng(41.0334, 28.8567));
        districtCoordinates.put("Bahçelievler", new LatLng(40.9945, 28.8482));
        districtCoordinates.put("Bakırköy", new LatLng(40.9730, 28.8149));
        districtCoordinates.put("Başakşehir", new LatLng(41.0938, 28.8028));
        districtCoordinates.put("Bayrampaşa", new LatLng(41.0392, 28.9052));
        districtCoordinates.put("Beşiktaş", new LatLng(41.0445, 29.0016));
        districtCoordinates.put("Beykoz", new LatLng(41.1582, 29.0834));
        districtCoordinates.put("Beylikdüzü", new LatLng(41.0015, 28.6451));
        districtCoordinates.put("Beyoğlu", new LatLng(41.0310, 28.9765));
        // Daha fazla ilçe ekleyin...
    }

    public static LatLng getCoordinates(String districtName) {
        return districtCoordinates.get(districtName);
    }
}
