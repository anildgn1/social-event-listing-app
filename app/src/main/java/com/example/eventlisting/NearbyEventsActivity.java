package com.example.eventlisting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.eventlisting.database.AppDatabase;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;


import java.util.ArrayList;
import java.util.List;

public class NearbyEventsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> allEvents = new ArrayList<>();
    private List<Event> filteredEvents = new ArrayList<>();
    private GoogleMap mMap;
    private TextView distanceTextView;
    private SeekBar distanceSeekBar;
    private double userLatitude = 41.0082; // İstanbul varsayılan konum
    private double userLongitude = 28.9784; // İstanbul varsayılan konum
    private int currentRadius = 10; // Varsayılan mesafe (10 km)
    private List<Event> myEventList;
    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_events);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        distanceTextView = findViewById(R.id.distanceTextView);
        distanceSeekBar = findViewById(R.id.distanceSeekBar);

        // Mesafe ayarı
        distanceSeekBar.setMax(200); // Maksimum 200 km
        distanceSeekBar.setProgress(currentRadius);
        distanceTextView.setText("Mesafe: " + currentRadius + " km");

        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentRadius = progress;
                distanceTextView.setText("Mesafe: " + currentRadius + " km");
                filterEventsByDistance();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Etkinlikleri yükle
        loadAllEvents();

        eventAdapter = new EventAdapter(filteredEvents, this, false);

        recyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnItemClickListener(position -> {
            Event selectedEvent = filteredEvents.get(position);
            Intent intent = new Intent(NearbyEventsActivity.this, EventDetailActivity.class);
            intent.putExtra("EVENT_TITLE", selectedEvent.getName());
            intent.putExtra("EVENT_DATE", selectedEvent.getDate());
            intent.putExtra("EVENT_DESCRIPTION", selectedEvent.getDescription());
            intent.putExtra("EVENT_CAPACITY", selectedEvent.getCapacity());
            intent.putExtra("EVENT_CITY", selectedEvent.getCity());
            intent.putExtra("EVENT_LATITUDE", selectedEvent.getLatitude());
            intent.putExtra("EVENT_LONGITUDE", selectedEvent.getLongitude());
            intent.putExtra("EVENT_DISTRICT", selectedEvent.getDistrict());

            if (selectedEvent.getImageUri() != null &&
                    !selectedEvent.getImageUri().trim().isEmpty() &&
                    !selectedEvent.getImageUri().equalsIgnoreCase("null")) {

                intent.putExtra("EVENT_IMAGE_URI", selectedEvent.getImageUri());

            } else {
                intent.putExtra("EVENT_IMAGE", selectedEvent.getImageResourceId());
            }

            startActivity(intent);
        });


        // Harita başlatma
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("NearbyEventsActivity", "SupportMapFragment is null");
            Toast.makeText(this, "Harita yüklenemedi.", Toast.LENGTH_SHORT).show();
        }

        filterEventsByDistance();
        getCurrentLocation(); // konumu başlat

    }

    private void loadAllEvents() {
        allEvents.clear();
        allEvents.addAll(AppDatabase.getInstance(getApplicationContext()).eventDao().getAllEvents());
    }




    private void filterEventsByDistance() {
        if (mMap != null) {
            mMap.clear(); // Tüm işaretçileri sil
        }

        filteredEvents.clear();

        for (Event event : allEvents) {
            double distance = calculateDistance(userLatitude, userLongitude, event.getLatitude(), event.getLongitude());
            if (distance <= currentRadius) {
                filteredEvents.add(event);
            }
        }

        if (mMap != null) {
            for (Event event : filteredEvents) {
                LatLng eventLocation = new LatLng(event.getLatitude(), event.getLongitude());
                mMap.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(event.getName())
                        .snippet("Tarih: " + event.getDate() + "\nKonum: " + event.getCity()));
            }
        }

        eventAdapter.notifyDataSetChanged(); // Listeyi güncelle
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371; // Dünya yarıçapı (km)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.setMyLocationEnabled(true); // Konum işaretini göster
        }


        filterEventsByDistance(); // Etkinlikleri göster
    }

    private void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                userLatitude = location.getLatitude();
                userLongitude = location.getLongitude();

                filterEventsByDistance(); // konum değişince etkinlikleri filtrele
            }

            @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override public void onProviderEnabled(String provider) {}
            @Override public void onProviderDisabled(String provider) {}
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (mMap != null) {
                    mMap.setMyLocationEnabled(true); // izin verilirse konumu aç
                }
            }
        } else {
            Toast.makeText(this, "Konum izni gerekli", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }



}
