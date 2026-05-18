package com.example.eventlisting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SortDialogFragment extends DialogFragment {

    private OnSortAppliedListener listener;

    public interface OnSortAppliedListener {
        void onSortApplied(String sortType);
    }

    public void setOnSortAppliedListener(OnSortAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sort, container, false);

        Button ascendingButton = view.findViewById(R.id.sortAscending);
        Button descendingButton = view.findViewById(R.id.sortDescending);

        ascendingButton.setOnClickListener(v -> {
            if (listener != null) listener.onSortApplied("Ascending");
            dismiss();
        });

        descendingButton.setOnClickListener(v -> {
            if (listener != null) listener.onSortApplied("Descending");
            dismiss();
        });

        return view;
    }
}
