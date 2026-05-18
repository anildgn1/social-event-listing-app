package com.example.eventlisting;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventlisting.database.AppDatabase;
import com.example.eventlisting.database.EventDao;
import com.example.eventlisting.database.User;
import com.example.eventlisting.database.UserDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;
import android.content.SharedPreferences;
import android.content.Context;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.text.TextWatcher;
import android.text.Editable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private EventDao eventDao;
    private UserDao userDao;
    private ActivityResultLauncher<Intent> addEventLauncher;
    private ImageView profileIcon;
    private FloatingActionButton fabAddEvent;
    private ImageButton btnNearby, btnMyEvents;
    private String currentUserEmail;
    private EditText searchEditText;
    private List<Event> allEventsList = new ArrayList<>();
    private ImageButton filterButton, sortButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEditText = findViewById(R.id.searchEditText);
        ImageButton optionsButton = findViewById(R.id.optionsButton);
        optionsButton.setOnClickListener(v -> {
            FilterSortDialog dialog = new FilterSortDialog((city, district, eventType, sortOrder, sortCriteria) -> {
                filterAndSortEvents(city, district, eventType, sortOrder, sortCriteria);
            });
            dialog.show(getSupportFragmentManager(), "FilterSortDialog");
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEventsBySearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        profileIcon = findViewById(R.id.btnProfile);
        fabAddEvent = findViewById(R.id.fabAddEvent);
        btnNearby = findViewById(R.id.btnNearby);
        btnMyEvents = findViewById(R.id.btnMyEvents);


        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        currentUserEmail = prefs.getString("user_email", null);

        profileIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));

        fabAddEvent.setOnClickListener(v -> {
            if (currentUserEmail != null) {
                Intent intent = new Intent(MainActivity.this, EventAddActivity.class);
                intent.putExtra("USER_EMAIL", currentUserEmail);
                addEventLauncher.launch(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginPromptActivity.class);
                startActivity(intent);
            }
        });



        btnNearby.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NearbyEventsActivity.class)));

        btnMyEvents.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MyEventsActivity.class);
            intent.putExtra("USER_EMAIL", currentUserEmail);
            startActivity(intent);
        });

        addEventLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        eventList = eventDao.getAllEvents();
                        eventAdapter.updateList(eventList);
                    }
                });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(this);
        eventDao = db.eventDao();
        userDao = db.userDao();

        if (userDao.getAllUsers().isEmpty()) {
            userDao.insert(new User("test@example.com", "password123", "Ali", "Yılmaz", "2000-01-01"));
            userDao.insert(new User("demo@example.com", "demo123", "Ayşe", "Demir", "1998-05-12"));
        }

        if (eventDao.getAllEvents().isEmpty()) {
            eventDao.insert(new Event("Grand Symphony Night", "2024-12-15", "Müzik tutkunları için unutulmaz bir geceye hazır olun! İstanbul Filarmoni Orkestrası, klasik müzikten modern eserler kadar geniş bir repertuarla sizi büyüleyecek. Bu özel etkinlikte ünlü şef John Williams yönetiminde dünyaca ünlü sanatçılar sahne alacak. Konser, Beşiktaş’taki prestijli Zorlu Performans Sanatları Merkezi’nde gerçekleşecek. Yerlerinizi hemen ayırtın!", 50, 41.0082, 28.9784, "İstanbul", "Beşiktaş", "Konser", true, R.drawable.orchestra, null, "sample@example.com"));
            eventDao.insert(new Event("Visionary Art Exhibition", "2024-12-20", "Sanatseverleri çağdaş sanatın en dikkat çekici eserleriyle buluşturuyoruz! Kadıköy’deki Barış Manço Kültür Merkezi’nde düzenlenecek sergide, ünlü yerli ve yabancı sanatçıların çarpıcı eserlerini keşfedeceksiniz. Sergi kapsamında düzenlenecek sanat konuşmaları ve atölyelerle sanata dair ufkunuzu genişletebilirsiniz. Sergi alanı ücretsizdir ve tüm ziyaretçilere açıktır.", 100, 40.9901, 29.0282, "İstanbul", "Kadıköy", "Sanat", false, R.drawable.art_exhibition, null, "sample@example.com"));
            eventDao.insert(new Event("Tech Horizons Meetup", "2024-12-18", "Teknoloji meraklılarını bir araya getiren bu etkinlikte, sektörün önde gelen isimleriyle tanışma ve geleceğin teknolojilerini keşfetme fırsatı bulacaksınız. Ankara Çankaya’da yer alan Bilkent Cyberpark’ta düzenlenen etkinlikte, yapay zeka, blokzincir ve nesnelerin interneti gibi konularda uzman sunumlar ve panel tartışmaları yer alacak. Ayrıca, networking seanslarıyla sektör profesyonelleriyle bağlantı kurabilirsiniz.", 30, 39.9208, 32.8541, "Ankara", "Çankaya", "Teknoloji", true, R.drawable.tech_meetup, null, "sample@example.com"));
            eventDao.insert(new Event("Belgrad Ormanı Yürüyüş Etkinliği", "2024-09-15", "Doğa severleri bir araya getiren rehber eşliğindeki bu yürüyüş etkinliği, keyifli bir piknikle tamamlanacak.", 75, 41.1865, 28.9786, "İstanbul", "Sarıyer", "Spor", true, R.drawable.belgrad_hiking_event, null, "sample@example.com"));
            eventDao.insert(new Event("Bostancı Sahil Bisiklet Turu", "2024-10-07", "Sağlıklı yaşamı teşvik eden bu bisiklet turu, sahil boyunca eşsiz bir parkur sunuyor.", 100, 40.9526, 29.0941, "İstanbul", "Kadıköy", "Spor", true, R.drawable.bostanci_cycling_tour, null, "sample@example.com"));
            eventDao.insert(new Event("Florya Plaj Voleybolu Turnuvası", "2024-08-30", "Florya sahilinde düzenlenecek olan bu turnuva, hem amatör hem profesyonel takımları ağırlayacak.", 200, 40.9712, 28.8175, "İstanbul", "Bakırköy", "Spor", false, R.drawable.florya_beach_volleyball, null, "sample@example.com"));
            eventDao.insert(new Event("Şişli Kişisel Gelişim Semineri", "2024-11-01", "Zaman yönetimi ve motivasyon temalı bu seminer, katılımcılara önemli yaşam becerileri kazandıracak.", 120, 41.0605, 28.9875, "İstanbul", "Şişli", "Eğitim", true, R.drawable.sisli_personal_development, null, "sample@example.com"));
            eventDao.insert(new Event("Fatih Tarihi Eserler Fotoğrafçılığı Atölyesi", "2024-10-18", "Fatih’in tarihi dokusunu fotoğraflamak isteyenler için düzenlenen bu etkinlikte profesyonel rehberler eşlik edecek.", 50, 41.0085, 28.9802, "İstanbul", "Fatih", "Eğitim", false, R.drawable.fatih_photography_workshop, null, "sample@example.com"));
            eventDao.insert(new Event("Beylikdüzü Robotik Kodlama Eğitimi", "2024-09-22", "Fatih’in tarihi dokusunu fotoğraflamak isteyenler için düzenlenen bu etkinlikte profesyonel rehberler eşlik edecek.", 40, 41.0012, 28.6416, "İstanbul", "Beylikdüzü", "Eğitim", true, R.drawable.beylikduzu_robotics_coding, null, "sample@example.com"));
            eventDao.insert(new Event("Eminönü Sokak Lezzetleri Festivali", "2024-11-15", "İstanbul’un eşsiz sokak lezzetleri Eminönü’nde ziyaretçilerle buluşuyor.", 300, 41.0165, 28.9742, "İstanbul", "Fatih", "Gastronomi", false, R.drawable.eminonu_food_festival, null, "sample@example.com"));
            eventDao.insert(new Event("Beşiktaş Çikolata ve Kahve Günleri", "2024-12-03", "Çikolata ve kahve tutkunlarını bir araya getiren bu etkinlikte özel tadımlar ve workshoplar düzenlenecek.", 200, 41.0422, 29.0042, "İstanbul", "Beşiktaş", "Gastronomi", true, R.drawable.besiktas_chocolate_coffee, null, "sample@example.com"));
            eventDao.insert(new Event("Kadıköy Vegan Yemek Festivali", "2024-10-10", "Moda Sahili’nde düzenlenecek olan bu festival, sağlıklı vegan yiyeceklerin tanıtılmasını amaçlıyor.", 150, 40.9840, 29.0255, "İstanbul", "Kadıköy", "Gastronomi", false, R.drawable.kadikoy_vegan_food, null, "sample@example.com"));
        }

        eventList = eventDao.getAllEvents();
        eventAdapter = new EventAdapter(eventList, this, false);
        recyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnItemClickListener(position -> {
            Event selectedEvent = eventAdapter.getEventAt(position);
            if (selectedEvent != null) {
                Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
                intent.putExtra("EVENT_TITLE", selectedEvent.getName());
                intent.putExtra("EVENT_DATE", selectedEvent.getDate());
                intent.putExtra("EVENT_DESCRIPTION", selectedEvent.getDescription());
                intent.putExtra("EVENT_CAPACITY", selectedEvent.getCapacity());
                intent.putExtra("EVENT_CITY", selectedEvent.getCity());
                intent.putExtra("EVENT_DISTRICT", selectedEvent.getDistrict());
                intent.putExtra("EVENT_LATITUDE", selectedEvent.getLatitude());
                intent.putExtra("EVENT_LONGITUDE", selectedEvent.getLongitude());
                if (selectedEvent.getImageUri() != null && !selectedEvent.getImageUri().trim().isEmpty() && !selectedEvent.getImageUri().equalsIgnoreCase("null")) {
                    intent.putExtra("EVENT_IMAGE_URI", selectedEvent.getImageUri());
                } else {
                    intent.putExtra("EVENT_IMAGE", selectedEvent.getImageResourceId());
                }

                startActivity(intent);
            }
        });
    }

    private void filterAndSortEvents(String city, String district, String eventType, String sortOrder, String sortCriteria) {
        List<Event> filteredList = new ArrayList<>();

        for (Event event : eventDao.getAllEvents()) {
            boolean matches = true;

            if (!city.isEmpty() && !event.getCity().equalsIgnoreCase(city)) {
                matches = false;
            }

            if (!district.isEmpty() && !event.getDistrict().equalsIgnoreCase(district)) {
                matches = false;
            }

            if (!eventType.isEmpty() && !event.getType().equalsIgnoreCase(eventType)) {
                matches = false;
            }

            if (matches) {
                filteredList.add(event);
            }
        }

        // Sıralama kriterine göre sırala
        if (sortOrder != null && !sortOrder.isEmpty() && sortCriteria != null && !sortCriteria.isEmpty()) {
            Comparator<Event> comparator = null;

            switch (sortCriteria) {
                case "Tarih":
                    comparator = (e1, e2) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                            Date date1 = sdf.parse(e1.getDate());
                            Date date2 = sdf.parse(e2.getDate());
                            return date1.compareTo(date2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    };
                    break;
                case "Kapasite":
                    comparator = Comparator.comparingInt(Event::getCapacity);
                    break;
                case "Şehir":
                    comparator = Comparator.comparing(Event::getCity, String.CASE_INSENSITIVE_ORDER);
                    break;
            }

            if (comparator != null) {
                if (sortOrder.equals("Descending")) {
                    comparator = comparator.reversed();
                }
                Collections.sort(filteredList, comparator);
            }
        }

        // Listeyi güncelle
        eventAdapter.updateList(filteredList);
    }

    private void filterEventsBySearch(String query) {
        List<Event> filteredList = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getName().toLowerCase().contains(query.toLowerCase()) ||
                    event.getDescription().toLowerCase().contains(query.toLowerCase()) ||
                    event.getCity().toLowerCase().contains(query.toLowerCase()) ||
                    (event.getDistrict() != null && event.getDistrict().toLowerCase().contains(query.toLowerCase()))) {
                filteredList.add(event);
            }
        }
        eventAdapter.updateList(filteredList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eventDao != null && eventAdapter != null) {
            eventList = eventDao.getAllEvents();
            eventAdapter.updateList(eventList);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.filter) {
            showFilterDialog();
            return true;
        } else if (itemId == R.id.sort) {
            showSortDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.filter_dialog, null);

        EditText startDateEditText = view.findViewById(R.id.startDateEditText);
        EditText endDateEditText = view.findViewById(R.id.endDateEditText);
        EditText minCapacityEditText = view.findViewById(R.id.minCapacityEditText);
        EditText maxCapacityEditText = view.findViewById(R.id.maxCapacityEditText);
        Spinner citySpinner = view.findViewById(R.id.citySpinner);
        Spinner districtSpinner = view.findViewById(R.id.districtSpinner);
        Spinner eventTypeSpinner = view.findViewById(R.id.eventTypeSpinner);
        Spinner paymentTypeSpinner = view.findViewById(R.id.paymentTypeSpinner);
        Button applyFilterButton = view.findViewById(R.id.applyFilterButton);
        Button clearFiltersButton = view.findViewById(R.id.clearFilters);

        ArrayAdapter<CharSequence> cityAdapter = ArrayAdapter.createFromResource(this,
                R.array.cities_array, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
        districtSpinner.setVisibility(View.GONE);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();
                if (selectedCity.equals("İstanbul")) {
                    districtSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> districtAdapter = ArrayAdapter.createFromResource(
                            MainActivity.this, R.array.istanbul_districts, android.R.layout.simple_spinner_item);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);
                } else {
                    districtSpinner.setVisibility(View.GONE);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                districtSpinner.setVisibility(View.GONE);
            }
        });

        ArrayAdapter<CharSequence> eventTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.event_types, android.R.layout.simple_spinner_item);
        eventTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(eventTypeAdapter);

        ArrayAdapter<CharSequence> paymentTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.payment_types, android.R.layout.simple_spinner_item);
        paymentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentTypeSpinner.setAdapter(paymentTypeAdapter);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        applyFilterButton.setOnClickListener(v -> {
            String startDate = startDateEditText.getText().toString().trim();
            String endDate = endDateEditText.getText().toString().trim();
            String minCapacityStr = minCapacityEditText.getText().toString().trim();
            String maxCapacityStr = maxCapacityEditText.getText().toString().trim();
            String selectedEventType = eventTypeSpinner.getSelectedItem().toString();
            String selectedCity = citySpinner.getSelectedItem().toString();
            String selectedDistrict = districtSpinner.getVisibility() == View.VISIBLE ?
                    districtSpinner.getSelectedItem().toString() : "";
            String selectedPaymentType = paymentTypeSpinner.getSelectedItem().toString();

            int minCapacity = minCapacityStr.isEmpty() ? 0 : Integer.parseInt(minCapacityStr);
            int maxCapacity = maxCapacityStr.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxCapacityStr);

            filterEvents(startDate, endDate, minCapacity, maxCapacity,
                    selectedEventType.equals("Etkinlik Türü") ? "" : selectedEventType,
                    selectedCity.equals("Tümü") ? "" : selectedCity,
                    selectedDistrict.equals("Tümü") ? "" : selectedDistrict,
                    selectedPaymentType.equals("Ücret Türü") ? "" : selectedPaymentType);
            dialog.dismiss();
        });

        clearFiltersButton.setOnClickListener(v -> {
            citySpinner.setSelection(0);
            districtSpinner.setVisibility(View.GONE);
            filterEvents("", "", 0, Integer.MAX_VALUE, "", "", "", "");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void filterEvents(String startDate, String endDate, int minCapacity, int maxCapacity,
                              String eventType, String city, String district, String paymentType) {
        List<Event> filteredList = new ArrayList<>();
        for (Event event : eventList) {
            boolean matchesDate = (startDate.isEmpty() || event.getDate().compareTo(startDate) >= 0) &&
                    (endDate.isEmpty() || event.getDate().compareTo(endDate) <= 0);
            boolean matchesCapacity = (minCapacity == 0 || event.getCapacity() >= minCapacity) &&
                    (maxCapacity == Integer.MAX_VALUE || event.getCapacity() <= maxCapacity);
            boolean matchesType = eventType.isEmpty() || event.getType().equals(eventType);
            boolean matchesCity = city.isEmpty() || event.getCity().equals(city);
            boolean matchesDistrict = district.isEmpty() || event.getDistrict().equals(district);
            boolean matchesPayment = paymentType.isEmpty() ||
                    (paymentType.equals("Ücretli") && event.isPaid()) ||
                    (paymentType.equals("Ücretsiz") && !event.isPaid());

            if (matchesDate && matchesCapacity && matchesType && matchesCity && matchesDistrict && matchesPayment) {
                filteredList.add(event);
            }
        }
        eventAdapter.updateList(filteredList);
    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sıralama Seçenekleri");
        String[] sortOptions = {"Tarihe Göre", "Kapasiteye Göre", "Şehre Göre"};
        builder.setItems(sortOptions, (dialog, which) -> {
            switch (which) {
                case 0:
                    eventList.sort((e1, e2) -> e1.getDate().compareTo(e2.getDate()));
                    break;
                case 1:
                    eventList.sort((e1, e2) -> Integer.compare(e1.getCapacity(), e2.getCapacity()));
                    break;
                case 2:
                    eventList.sort((e1, e2) -> e1.getCity().compareTo(e2.getCity()));
                    break;
            }
            eventAdapter.notifyDataSetChanged();
        });
        builder.show();
    }
}
