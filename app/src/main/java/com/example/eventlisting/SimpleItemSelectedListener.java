package com.example.eventlisting;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {

    private final View.OnClickListener listener;

    public SimpleItemSelectedListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        listener.onClick(view);
    }

    @Override public void onNothingSelected(AdapterView<?> parent) { }
}
