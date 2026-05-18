package com.example.eventlisting;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Map;
import java.util.HashMap;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.eventlisting.database.AppDatabase;
import com.example.eventlisting.database.EventDao;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;


public class EventAddActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;

    private TextInputEditText titleEditText, dateEditText, descriptionEditText, capacityEditText;
    private Spinner citySpinner, districtSpinner, eventTypeSpinner;
    private ImageView selectedImageView;
    private Button addEventButton;
    private Uri selectedImageUri;

    // Düzenleme modu için eklendi
    private Event editingEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_add);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
            }
        }

        String userEmail = getIntent().getStringExtra("USER_EMAIL");
        int editingEventId = getIntent().getIntExtra("EDIT_EVENT_ID", -1);

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Kullanıcı girişi yapılmadan etkinlik eklenemez.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // UI
        titleEditText = findViewById(R.id.titleEditText);
        dateEditText = findViewById(R.id.dateEditText);
        dateEditText.setFocusable(false);
        dateEditText.setOnClickListener(v -> {
            CustomDatePickerDialog dialog = new CustomDatePickerDialog(this, selectedDate -> {
                dateEditText.setText(selectedDate);
            });
            dialog.show();
        });

        descriptionEditText = findViewById(R.id.descriptionEditText);
        capacityEditText = findViewById(R.id.capacityEditText);
        citySpinner = findViewById(R.id.citySpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        eventTypeSpinner = findViewById(R.id.eventTypeSpinner);
        addEventButton = findViewById(R.id.addEventButton);
        selectedImageView = findViewById(R.id.selectedImageView);
        Button selectImageButton = findViewById(R.id.selectImageButton);

        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, IMAGE_PICK_CODE);
        });


        // Spinner adapter'ları
        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this, R.array.cities_array, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter.createFromResource(this, R.array.event_types, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = citySpinner.getSelectedItem().toString();
                if ("İstanbul".equalsIgnoreCase(selectedCity)) {
                    districtSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(EventAddActivity.this, R.array.istanbul_districts, android.R.layout.simple_spinner_item);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);
                } else {
                    districtSpinner.setVisibility(View.GONE);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Eğer düzenleme yapılacaksa verileri getir ve doldur
        if (editingEventId != -1) {
            EventDao dao = AppDatabase.getInstance(this).eventDao();
            editingEvent = dao.getEventById(editingEventId);
            if (editingEvent != null) {
                titleEditText.setText(editingEvent.getName());
                dateEditText.setText(editingEvent.getDate());
                descriptionEditText.setText(editingEvent.getDescription());
                capacityEditText.setText(String.valueOf(editingEvent.getCapacity()));
                selectedImageUri = Uri.parse(editingEvent.getImageUri());
                if (selectedImageUri != null) {
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(selectedImageView);
                    selectedImageView.setVisibility(View.VISIBLE);
                }

                // Şehir
                int cityIndex = getSpinnerIndex(citySpinner, editingEvent.getCity());
                citySpinner.setSelection(cityIndex);

                // İlçe (spinner hazır olduğunda ayarlamak için gecikmeli)
                citySpinner.post(() -> {
                    if ("İstanbul".equalsIgnoreCase(editingEvent.getCity())) {
                        districtSpinner.setVisibility(View.VISIBLE);
                        ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(EventAddActivity.this, R.array.istanbul_districts, android.R.layout.simple_spinner_item);
                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        districtSpinner.setAdapter(districtAdapter);
                        districtSpinner.post(() -> {
                            if (editingEvent.getDistrict() != null) {
                                districtSpinner.setSelection(getSpinnerIndex(districtSpinner, editingEvent.getDistrict()));
                            }
                        });
                    }
                });

                // Tür
                eventTypeSpinner.setSelection(getSpinnerIndex(eventTypeSpinner, editingEvent.getType()));

                addEventButton.setText("Etkinliği Güncelle");
            }
        }

        // Ekle/Güncelle işlemi
        addEventButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String date = dateEditText.getText().toString().trim();
            String desc = descriptionEditText.getText().toString().trim();
            String capacityStr = capacityEditText.getText().toString().trim();
            String city = citySpinner.getSelectedItem().toString();
            String district = (districtSpinner.getVisibility() == View.VISIBLE) ? districtSpinner.getSelectedItem().toString() : null;
            String type = eventTypeSpinner.getSelectedItem().toString();

            if (title.isEmpty() || date.isEmpty() || desc.isEmpty() || capacityStr.isEmpty() ||
                    (districtSpinner.getVisibility() == View.VISIBLE && district == null)) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            int capacity = Integer.parseInt(capacityStr);
            LatLng coordinates = "İstanbul".equalsIgnoreCase(city) ?
                    getCoordinatesForDistrict(district.toLowerCase().trim()) :
                    getCoordinatesForCity(city.toLowerCase().trim());

            if (coordinates.latitude == 0 && coordinates.longitude == 0) {
                Toast.makeText(this, "Geçersiz konum", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            EventDao dao = db.eventDao();

            if (editingEvent != null) {
                editingEvent.setName(title);
                editingEvent.setDate(date);
                editingEvent.setDescription(desc);
                editingEvent.setCapacity(capacity);
                editingEvent.setLatitude(coordinates.latitude);
                editingEvent.setLongitude(coordinates.longitude);
                editingEvent.setCity(city);
                editingEvent.setDistrict(district);
                editingEvent.setType(type);
                editingEvent.setImageUri(selectedImageUri != null ? selectedImageUri.toString() : null);
                dao.update(editingEvent);
                Toast.makeText(this, "Etkinlik güncellendi", Toast.LENGTH_SHORT).show();
            } else {
                Event newEvent = new Event(title, date, desc, capacity, coordinates.latitude, coordinates.longitude,
                        city, district, type, true, 0,
                        selectedImageUri != null ? selectedImageUri.toString() : null, userEmail);
                dao.insert(newEvent);
                Toast.makeText(this, "Etkinlik eklendi", Toast.LENGTH_SHORT).show();
            }

            setResult(RESULT_OK);
            finish();
        });
    }

    private int getSpinnerIndex(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }

    private LatLng getCoordinatesForCity(String city) {
        Map<String, LatLng> map = new HashMap<>();
        map.put("istanbul", new LatLng(41.0082, 28.9784));
        map.put("ankara", new LatLng(39.9208, 32.8541));
        map.put("izmir", new LatLng(38.4192, 27.1287));
        return map.getOrDefault(city, new LatLng(0, 0));
    }

    private LatLng getCoordinatesForDistrict(String district) {
        Map<String, LatLng> districtCoordinates = new HashMap<>();
        districtCoordinates.put("adalar", new LatLng(40.8742, 29.1259));
        districtCoordinates.put("arnavutköy", new LatLng(41.2019, 28.7403));
        districtCoordinates.put("ataşehir", new LatLng(40.9923, 29.1278));
        districtCoordinates.put("avcılar", new LatLng(40.9797, 28.7214));
        districtCoordinates.put("bağcılar", new LatLng(41.0334, 28.8567));
        districtCoordinates.put("bahçelievler", new LatLng(40.9945, 28.8482));
        districtCoordinates.put("bakırköy", new LatLng(40.9730, 28.8149));
        districtCoordinates.put("başakşehir", new LatLng(41.0938, 28.8028));
        districtCoordinates.put("bayrampaşa", new LatLng(41.0392, 28.9052));
        districtCoordinates.put("beşiktaş", new LatLng(41.0445, 29.0016));
        districtCoordinates.put("beykoz", new LatLng(41.1582, 29.0834));
        districtCoordinates.put("beylikdüzü", new LatLng(41.0015, 28.6451));
        districtCoordinates.put("beyoğlu", new LatLng(41.0310, 28.9765));
        districtCoordinates.put("büyükçekmece", new LatLng(41.0215, 28.5949));
        districtCoordinates.put("çekmeköy", new LatLng(41.0260, 29.1847));
        districtCoordinates.put("esenler", new LatLng(41.0434, 28.8763));
        districtCoordinates.put("esenyurt", new LatLng(41.0330, 28.6753));
        districtCoordinates.put("eyüpsultan", new LatLng(41.0460, 28.9122));
        districtCoordinates.put("fatih", new LatLng(41.0178, 28.9497));
        districtCoordinates.put("gaziosmanpaşa", new LatLng(41.0878, 28.9124));
        districtCoordinates.put("güngören", new LatLng(41.0219, 28.8794));
        districtCoordinates.put("kadıköy", new LatLng(40.9792, 29.0876));
        districtCoordinates.put("kağıthane", new LatLng(41.0917, 28.9731));
        districtCoordinates.put("kartal", new LatLng(40.8990, 29.1879));
        districtCoordinates.put("küçükçekmece", new LatLng(41.0062, 28.7896));
        districtCoordinates.put("maltepe", new LatLng(40.9369, 29.1435));
        districtCoordinates.put("pendik", new LatLng(40.8771, 29.2467));
        districtCoordinates.put("sancaktepe", new LatLng(41.0030, 29.2303));
        districtCoordinates.put("sarıyer", new LatLng(41.1680, 29.0532));
        districtCoordinates.put("silivri", new LatLng(41.0731, 28.2421));
        districtCoordinates.put("sultanbeyli", new LatLng(40.9612, 29.2663));
        districtCoordinates.put("sultangazi", new LatLng(41.0985, 28.8538));
        districtCoordinates.put("şile", new LatLng(41.1750, 29.6147));
        districtCoordinates.put("şişli", new LatLng(41.0605, 28.9876));
        districtCoordinates.put("tuzla", new LatLng(40.8625, 29.3014));
        districtCoordinates.put("ümraniye", new LatLng(41.0165, 29.1235));
        districtCoordinates.put("üsküdar", new LatLng(41.0321, 29.0261));
        districtCoordinates.put("zeytinburnu", new LatLng(40.9945, 28.9003));
        return districtCoordinates.getOrDefault(district.toLowerCase().trim(), new LatLng(0, 0));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // URI'ye kalıcı erişim izni ver
            final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);

            selectedImageView.setImageURI(selectedImageUri);
            selectedImageView.setVisibility(View.VISIBLE);
        }
    }

}
