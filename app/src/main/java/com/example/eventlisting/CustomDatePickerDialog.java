package com.example.eventlisting;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.CalendarView;
import java.util.Calendar;
import java.util.Locale;

public class CustomDatePickerDialog extends Dialog {

    private CalendarView calendarView;
    private Spinner yearSpinner, monthSpinner;
    private Button selectButton, cancelButton;

    private OnDateSelectedListener listener;
    private long selectedDateMillis;  // seçilen günü tutmak için

    public interface OnDateSelectedListener {
        void onDateSelected(String formattedDate);
    }

    public CustomDatePickerDialog(Context context, OnDateSelectedListener listener) {
        super(context);
        this.listener = listener;
        setContentView(R.layout.dialog_custom_date_picker);

        yearSpinner = findViewById(R.id.yearSpinner);
        monthSpinner = findViewById(R.id.monthSpinner);
        calendarView = findViewById(R.id.calendarView);
        selectButton = findViewById(R.id.btnSelectDate);
        cancelButton = findViewById(R.id.btnCancelDate);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[101];
        for (int i = 0; i < 101; i++) {
            years[i] = currentYear - 80 + i;
        }

        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setSelection(80);

        String[] turkishMonths = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
                "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, turkishMonths);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);
        monthSpinner.setSelection(Calendar.getInstance().get(Calendar.MONTH));

        // Güncelleme sadece ay/yıl seçimini etkileyecek
        View.OnClickListener updateCalendar = v -> {
            int selectedYear = (Integer) yearSpinner.getSelectedItem();
            int selectedMonth = monthSpinner.getSelectedItemPosition();

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDateMillis); // günü koruyarak güncelle
            cal.set(Calendar.YEAR, selectedYear);
            cal.set(Calendar.MONTH, selectedMonth);

            calendarView.setDate(cal.getTimeInMillis(), false, false); // animasyonsuz
        };

        monthSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(updateCalendar));
        yearSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(updateCalendar));

        // Gün seçimi değiştiğinde güncel tarihi tut
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(Calendar.YEAR, year);
            selected.set(Calendar.MONTH, month);
            selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            selectedDateMillis = selected.getTimeInMillis();
        });

        // Başlangıçta bugüne ayarlı olsun
        selectedDateMillis = calendarView.getDate();

        selectButton.setOnClickListener(v -> {
            Calendar selected = Calendar.getInstance();
            selected.setTimeInMillis(selectedDateMillis);
            int year = selected.get(Calendar.YEAR);
            int month = selected.get(Calendar.MONTH) + 1;
            int day = selected.get(Calendar.DAY_OF_MONTH);
            listener.onDateSelected(String.format(Locale.getDefault(), "%02d.%02d.%04d", day, month, year));
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
}
