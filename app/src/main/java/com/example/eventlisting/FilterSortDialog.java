package com.example.eventlisting;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class FilterSortDialog extends DialogFragment {

    private Spinner citySpinner, districtSpinner, eventTypeSpinner, sortCriteriaSpinner;
    private String selectedSortOrder = ""; // "Ascending" veya "Descending"
    private final OnFilterSortListener listener;

    public interface OnFilterSortListener {
        void onFilterSortApplied(String city, String district, String eventType, String sortOrder, String sortCriteria);
    }

    public FilterSortDialog(OnFilterSortListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_filter_sort, null);

        citySpinner = view.findViewById(R.id.citySpinner);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        eventTypeSpinner = view.findViewById(R.id.eventTypeSpinner);
        sortCriteriaSpinner = view.findViewById(R.id.sortCriteriaSpinner);

        Button btnSortAsc = view.findViewById(R.id.btnSortAsc);
        Button btnSortDesc = view.findViewById(R.id.btnSortDesc);
        Button btnApply = view.findViewById(R.id.btnApplyFilterSort);
        Button btnClear = view.findViewById(R.id.btnClearFilterSort);

        // Şehir Spinner
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.cities_array));
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        // İlçe Spinner (sadece İstanbul için)
        citySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String city = citySpinner.getSelectedItem().toString();
                if (city.equals("İstanbul")) {
                    districtSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_item,
                            getResources().getStringArray(R.array.istanbul_districts));
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);
                } else {
                    districtSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                districtSpinner.setVisibility(View.GONE);
            }
        });

        // Etkinlik Türü Spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.event_types));
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventTypeSpinner.setAdapter(typeAdapter);

        // Sıralama Kriteri Spinner
        ArrayAdapter<String> criteriaAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Tarih", "Kapasite", "Şehir"});
        criteriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortCriteriaSpinner.setAdapter(criteriaAdapter);

        // Sıralama butonları
        btnSortAsc.setOnClickListener(v -> selectedSortOrder = "Ascending");
        btnSortDesc.setOnClickListener(v -> selectedSortOrder = "Descending");

        // Uygula
        btnApply.setOnClickListener(v -> {
            String city = citySpinner.getSelectedItem().toString();
            String district = (districtSpinner.getVisibility() == View.VISIBLE) ? districtSpinner.getSelectedItem().toString() : "";
            String eventType = eventTypeSpinner.getSelectedItem().toString();
            String sortCriteria = sortCriteriaSpinner.getSelectedItem().toString();

            if (listener != null) {
                listener.onFilterSortApplied(
                        city.equals("İl seçiniz") ? "" : city,
                        district.equals("İlçe seçiniz") ? "" : district,
                        eventType.equals("Etkinlik Türü") ? "" : eventType,
                        selectedSortOrder,
                        sortCriteria
                );
            }
            dismiss();
        });

        // Temizle
        btnClear.setOnClickListener(v -> {
            citySpinner.setSelection(0);
            districtSpinner.setVisibility(View.GONE);
            eventTypeSpinner.setSelection(0);
            sortCriteriaSpinner.setSelection(0);
            selectedSortOrder = "";
            if (listener != null) {
                listener.onFilterSortApplied("", "", "", "", "");
            }
            dismiss();
        });

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .create();
    }
}
