package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.Utils;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.components.RecyclerViewDivider;
import com.martinwalls.nea.components.SwipeToDeleteCallback;
import com.martinwalls.nea.db.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class EditMeatTypesActivity extends AppCompatActivity
        implements AddNewMeatTypeDialog.AddNewMeatTypeListener {

    private DBHandler dbHandler;

    private MeatTypesAdapter meatTypesAdapter;
    private List<String> meatTypesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meat_types);

        getSupportActionBar().setTitle(R.string.meat_types_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        meatTypesAdapter = new MeatTypesAdapter(meatTypesList, this);
        recyclerView.setAdapter(meatTypesAdapter);
        loadMeatTypes();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(this, R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(meatTypesAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            DialogFragment addNewMeatTypeDialog = new AddNewMeatTypeDialog();
            addNewMeatTypeDialog.show(getSupportFragmentManager(), "add_new_meat_type");
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(this);
        }
        loadMeatTypes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAddNewMeatTypeDoneAction(String meatType) {
        dbHandler.addMeatType(meatType);
        loadMeatTypes();
    }

    private void loadMeatTypes() {
        meatTypesList.clear();
        meatTypesList.addAll(Utils.mergeSort(dbHandler.getAllMeatTypes()));
        meatTypesAdapter.notifyDataSetChanged();
    }
}
