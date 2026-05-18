package com.example.eventlisting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlisting.database.AppDatabase;
import com.example.eventlisting.database.EventDao;

import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EventAdapter eventAdapter;
    private List<Event> myEventList;
    private EventDao eventDao;
    private LinearLayout loginWarningLayout;
    private TextView warningText, emptyEventText;
    private Button loginRedirectButton, addEventFromMyEventsButton;
    private LinearLayout emptyEventLayout;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        recyclerView = findViewById(R.id.recyclerView);
        warningText = findViewById(R.id.warningText);
        loginRedirectButton = findViewById(R.id.loginRedirectButton);
        emptyEventText = findViewById(R.id.emptyEventText);
        addEventFromMyEventsButton = findViewById(R.id.addEventFromMyEventsButton);
        emptyEventLayout = findViewById(R.id.emptyEventLayout);
        loginWarningLayout = findViewById(R.id.loginWarningLayout);

        eventDao = AppDatabase.getInstance(this).eventDao();

        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userEmail = prefs.getString("user_email", null);

        if (userEmail == null || userEmail.isEmpty()) {
            showLoginWarning();
        } else {
            setupEventList();
        }

        addEventFromMyEventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventsActivity.this, EventAddActivity.class);
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userEmail == null || userEmail.isEmpty()) {
            showLoginWarning();
        } else {
            refreshEventList();
        }
    }

    private void showLoginWarning() {
        recyclerView.setVisibility(View.GONE);
        emptyEventLayout.setVisibility(View.GONE);
        warningText.setVisibility(View.VISIBLE);
        loginRedirectButton.setVisibility(View.VISIBLE);
        loginWarningLayout.setVisibility(View.VISIBLE);

        loginRedirectButton.setOnClickListener(v -> {
            Intent intent = new Intent(MyEventsActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupEventList() {
        recyclerView.setVisibility(View.VISIBLE);
        warningText.setVisibility(View.GONE);
        loginRedirectButton.setVisibility(View.GONE);

        myEventList = eventDao.getEventsByUserEmail(userEmail);
        if (myEventList == null) myEventList = new ArrayList<>();

        if (myEventList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyEventLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyEventLayout.setVisibility(View.GONE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(myEventList, this, true);
        recyclerView.setAdapter(eventAdapter);

        eventAdapter.setOnItemClickListener(position -> {
            Event selectedEvent = eventAdapter.getEventAt(position);
            Intent intent = new Intent(MyEventsActivity.this, EventAddActivity.class);
            intent.putExtra("EDIT_EVENT_ID", selectedEvent.getId());
            intent.putExtra("USER_EMAIL", userEmail);
            startActivity(intent);
        });

        eventAdapter.setOnEventActionListener(new EventAdapter.OnEventActionListener() {
            @Override
            public void onEditClick(int position) {
                Event selectedEvent = eventAdapter.getEventAt(position);
                Intent intent = new Intent(MyEventsActivity.this, EventAddActivity.class);
                intent.putExtra("EDIT_EVENT_ID", selectedEvent.getId());
                intent.putExtra("USER_EMAIL", userEmail);
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(int position) {
                Event selectedEvent = eventAdapter.getEventAt(position);
                eventDao.delete(selectedEvent);
                Toast.makeText(MyEventsActivity.this, "Etkinlik silindi", Toast.LENGTH_SHORT).show();
                refreshEventList();
            }
        });
    }

    private void refreshEventList() {
        if (eventDao == null || userEmail == null) return;

        myEventList = eventDao.getEventsByUserEmail(userEmail);
        if (myEventList == null) myEventList = new ArrayList<>();

        if (myEventList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyEventLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyEventLayout.setVisibility(View.GONE);
        }

        if (eventAdapter != null) {
            eventAdapter.updateList(myEventList);
        }
    }
}
