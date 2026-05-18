package com.example.eventlisting;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.net.Uri;
import com.bumptech.glide.Glide;

public class EventDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView eventTitleTextView, eventDateTextView, eventDescriptionTextView, eventCapacityTextView, eventCityTextView, eventDistrictTextView;
    private ImageView eventImageView;
    private MapView eventMapView;
    private GoogleMap googleMap;

    private double eventLatitude;
    private double eventLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        // Initialize views
        eventTitleTextView = findViewById(R.id.eventTitleTextView);
        eventDateTextView = findViewById(R.id.eventDateTextView);
        eventDescriptionTextView = findViewById(R.id.eventDescriptionTextView);
        eventCapacityTextView = findViewById(R.id.eventCapacityTextView);
        eventCityTextView = findViewById(R.id.eventCityTextView);
        eventDistrictTextView = findViewById(R.id.eventDistrictTextView);
        eventImageView = findViewById(R.id.eventImageView);
        eventMapView = findViewById(R.id.eventMapView);


        // Initialize MapView
        eventMapView.onCreate(savedInstanceState);
        eventMapView.getMapAsync(this);

        // Load data from intent
        if (getIntent() != null) {
            String title = getIntent().getStringExtra("EVENT_TITLE");
            String date = getIntent().getStringExtra("EVENT_DATE");
            String description = getIntent().getStringExtra("EVENT_DESCRIPTION");
            int capacity = getIntent().getIntExtra("EVENT_CAPACITY", 0);
            int imageResourceId = getIntent().getIntExtra("EVENT_IMAGE", -1); // Default değer -1
            String imageUriString = getIntent().getStringExtra("EVENT_IMAGE_URI");
            eventLatitude = getIntent().getDoubleExtra("EVENT_LATITUDE", 0.0);
            eventLongitude = getIntent().getDoubleExtra("EVENT_LONGITUDE", 0.0);
            String city = getIntent().getStringExtra("EVENT_CITY");
            String district = getIntent().getStringExtra("EVENT_DISTRICT");

            // Bind data to views
            eventTitleTextView.setText(title);
            eventDateTextView.setText(date);
            eventDescriptionTextView.setText(description);
            eventCapacityTextView.setText("Capacity: " + capacity);
            eventCityTextView.setText("Şehir: " + city); // Şehir bilgisi
            eventDistrictTextView.setText("İlçe: " + district); // İlçe bilgisi

            if (imageUriString != null && !imageUriString.trim().isEmpty() && !imageUriString.equalsIgnoreCase("null")) {
                Glide.with(this)
                        .load(Uri.parse(imageUriString))
                        .placeholder(R.drawable.sample_event_image) // Yüklenene kadar gösterilecek
                        .error(R.drawable.sample_event_image)       // Hata olursa gösterilecek
                        .into(eventImageView);
            } else if (imageResourceId != -1) {
                eventImageView.setImageResource(imageResourceId);
            } else {
                eventImageView.setImageResource(R.drawable.sample_event_image);
            }

        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        // Show the event location on the map
        LatLng eventLocation = new LatLng(eventLatitude, eventLongitude);
        googleMap.addMarker(new MarkerOptions().position(eventLocation).title("Event Location"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15));
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eventMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        eventMapView.onSaveInstanceState(outState);
    }
}
