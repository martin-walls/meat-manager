package com.martinwalls.meatmanager.ui.meatTypes;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.db.DBHandler;
import com.martinwalls.meatmanager.ui.common.recyclerview.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewDivider;
import com.martinwalls.meatmanager.ui.common.recyclerview.SwipeToDeleteCallback;
import com.martinwalls.meatmanager.util.SortUtils;

import java.util.ArrayList;
import java.util.List;

public class EditMeatTypesActivity extends AppCompatActivity
        implements AddNewMeatTypeDialog.AddNewMeatTypeListener {

    private DBHandler dbHandler; //todo ViewModel

    private MeatTypesAdapter meatTypesAdapter;
    private List<String> meatTypesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_meat_types);

        getSupportActionBar().setTitle(R.string.meat_types_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHandler = new DBHandler(this);

        initMeatTypesListView();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showNewMeatTypeDialog());
    }

    @Override
    public void onResume() {
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
        boolean success = dbHandler.addMeatType(meatType);
        if (success) {
            Toast.makeText(this, getString(R.string.add_meat_type_success, meatType),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.add_meat_type_error, meatType),
                    Toast.LENGTH_SHORT).show();
        }
        loadMeatTypes();
    }

    /**
     * Initialises the view to show the list of meat types.
     */
    private void initMeatTypesListView() {
        CustomRecyclerView recyclerView = findViewById(R.id.recycler_view);
        TextView emptyView = findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        meatTypesAdapter = new MeatTypesAdapter(meatTypesList, this);
        recyclerView.setAdapter(meatTypesAdapter);
        loadMeatTypes();

        // add item dividers
        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(this, R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // set swipe to delete action
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new SwipeToDeleteCallback(meatTypesAdapter, this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Shows a {@link AddNewMeatTypeDialog} to allow the user to add a new
     * meat type.
     */
    private void showNewMeatTypeDialog() {
        DialogFragment addNewMeatTypeDialog = new AddNewMeatTypeDialog();
        addNewMeatTypeDialog.show(getSupportFragmentManager(), "add_new_meat_type");
    }

    /**
     * Gets all meat types from the database. Reloads the layout to show the
     * updated data.
     */
    private void loadMeatTypes() {
        meatTypesList.clear();
        meatTypesList.addAll(SortUtils.mergeSort(dbHandler.getAllMeatTypes()));
        meatTypesAdapter.notifyDataSetChanged();
    }
}
