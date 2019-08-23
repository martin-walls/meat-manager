package com.martinwalls.nea.stock;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.components.CustomRecyclerView;
import com.martinwalls.nea.db.DBHandler;

import java.util.ArrayList;
import java.util.List;

public class EditMeatTypesActivity extends AppCompatActivity {

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

        meatTypesAdapter = new MeatTypesAdapter(meatTypesList);
        recyclerView.setAdapter(meatTypesAdapter);
        loadMeatTypes();

//        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(this, R.drawable.divider_thin);
//        recyclerView.addItemDecoration(recyclerViewDivider);
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            //todo start new meat type activity
            Toast.makeText(this, "FAB", Toast.LENGTH_SHORT).show();
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

    private void loadMeatTypes() {
        meatTypesList.clear();
        meatTypesList.addAll(dbHandler.getAllMeatTypes());
        meatTypesAdapter.notifyDataSetChanged();
    }
}
