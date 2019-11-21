package com.martinwalls.nea.ui.contracts;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.ui.misc.CustomRecyclerView;
import com.martinwalls.nea.ui.misc.RecyclerViewDivider;
import com.martinwalls.nea.util.SortUtils;
import com.martinwalls.nea.util.undo.UndoStack;

import java.util.ArrayList;
import java.util.List;

public class ContractsFragment extends Fragment
        implements ContractsAdapter.ContractsAdapterListener {

    private DBHandler dbHandler;

    private ContractsAdapter contractsAdapter;
    private List<Contract> contractList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.contracts_title);
        View fragmentView = inflater.inflate(R.layout.fragment_contracts, container, false);

        dbHandler = new DBHandler(getContext());

        initContractsList(fragmentView);
        loadContracts();

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startNewContractActivity());

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (dbHandler == null) {
            dbHandler = new DBHandler(getContext());
        }
        loadContracts();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_contracts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_undo:
                UndoStack.getInstance().undo(getContext());
                loadContracts();
                return true;
            case R.id.action_redo:
                UndoStack.getInstance().redo(getContext());
                loadContracts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This is called when a contract item is clicked in the list. Opens the
     * detail page for that contract.
     */
    @Override
    public void onContractClicked(Contract contract) {
        Intent detailIntent = new Intent(getContext(), ContractDetailActivity.class);
        detailIntent.putExtra(ContractDetailActivity.EXTRA_CONTRACT_ID,
                contract.getContractId());
        startActivity(detailIntent);
    }

    /**
     * Initialises the contracts list. Doesn't load any data, this should be
     * done by calling {@link #loadContracts()}.
     */
    private void initContractsList(View fragmentView) {
        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        contractsAdapter = new ContractsAdapter(contractList, this);
        recyclerView.setAdapter(contractsAdapter);

        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    /**
     * Gets all contracts in the database, sorts them and updates the view to
     * show them.
     */
    private void loadContracts() {
        contractList.clear();
        contractList.addAll(SortUtils.mergeSort(dbHandler.getAllContracts(),
                Contract.comparatorDate()));
        contractsAdapter.notifyDataSetChanged();
    }

    /**
     * Opens {@link EditContractActivity} so the user can add a new contract.
     */
    private void startNewContractActivity() {
        Intent newContractIntent = new Intent(getContext(), EditContractActivity.class);
        newContractIntent.putExtra(EditContractActivity.EXTRA_EDIT_TYPE,
                EditContractActivity.EDIT_TYPE_NEW);
        startActivity(newContractIntent);
    }
}
