package com.martinwalls.nea;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class SelectItemDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.dialog_select_item, null);

        List<String> dummyOptions = new ArrayList<>();
        dummyOptions.add("gasp");
        dummyOptions.add("cake");
        dummyOptions.add("physical");
        dummyOptions.add("executrix");
        dummyOptions.add("text");
        dummyOptions.add("vegetarian");
        dummyOptions.add("jockey");
        dummyOptions.add("distort");
        dummyOptions.add("champagne");
        dummyOptions.add("tread");


        CustomRecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_results);

        ItemAdapter adapter = new ItemAdapter(dummyOptions);
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(dialogView.findViewById(R.id.empty));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        builder.setView(dialogView);

        return builder.create();
    }
}
