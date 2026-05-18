package com.example.eventlisting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterDialogFragment extends DialogFragment {

    private Spinner citySpinner;
    private Spinner districtSpinner;
    private OnFilterAppliedListener listener;

    public interface OnFilterAppliedListener {
        void onFilterApplied(String filterType, String city, String district);
    }

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_dialog, container, false);

        citySpinner = view.findViewById(R.id.citySpinner);
        districtSpinner = view.findViewById(R.id.districtSpinner);
        Button applyFilterButton = view.findViewById(R.id.applyFilterButton);
        Button clearFiltersButton = view.findViewById(R.id.clearFilters);

        // Şehir Spinner'ı için verileri strings.xml'den yükle
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.cities_array) // cities_array'i strings.xml'den al
        );
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);

        // İlçe Spinner'ı başlangıçta görünmesin
        districtSpinner.setVisibility(View.GONE);

        // Şehir seçildiğinde İstanbul'a özel ilçeleri doldurma
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();

                if (selectedCity.equals("Tümü")) {
                    // "Tümü" seçilmişse ilçeyi gizle
                    districtSpinner.setVisibility(View.GONE);
                } else if (selectedCity.equals("İstanbul")) {
                    // İstanbul seçilmişse ilçeleri göster
                    districtSpinner.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            getResources().getStringArray(R.array.istanbul_districts) // İstanbul ilçelerini strings.xml'den al
                    );
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    districtSpinner.setAdapter(districtAdapter);
                } else {
                    // Diğer şehirlerde ilçeleri gizle
                    districtSpinner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                districtSpinner.setVisibility(View.GONE);
            }
        });

        // Filtre Uygula Butonu
        applyFilterButton.setOnClickListener(v -> {
            String selectedCity = citySpinner.getSelectedItem().toString();
            String selectedDistrict = districtSpinner.getVisibility() == View.VISIBLE ?
                    districtSpinner.getSelectedItem().toString() : "";

            if (listener != null) {
                listener.onFilterApplied(
                        "Apply Filters",
                        selectedCity.equals("Tümü") ? "" : selectedCity,
                        selectedDistrict
                );
            }
            dismiss();
        });

        // Filtreleri Temizle Butonu
        clearFiltersButton.setOnClickListener(v -> {
            citySpinner.setSelection(0); // Şehir spinner'ını sıfırla
            districtSpinner.setVisibility(View.GONE); // İlçe spinner'ını gizle

            if (listener != null) {
                listener.onFilterApplied("Clear", "", "");
            }
            dismiss();
        });

        return view;
    }
}
