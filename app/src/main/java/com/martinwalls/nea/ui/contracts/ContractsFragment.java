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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.nea.R;
import com.martinwalls.nea.ui.misc.CustomRecyclerView;
import com.martinwalls.nea.ui.misc.RecyclerViewDivider;
import com.martinwalls.nea.data.db.DBHandler;
import com.martinwalls.nea.data.models.Contract;
import com.martinwalls.nea.util.Utils;

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

        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        contractsAdapter = new ContractsAdapter(contractList, this);
        recyclerView.setAdapter(contractsAdapter);
        loadContracts();

        RecyclerViewDivider recyclerViewDivider = new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent newContractIntent = new Intent(getContext(), NewContractActivity.class);
            startActivity(newContractIntent);
        });

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
                Toast.makeText(getContext(), "UNDO", Toast.LENGTH_SHORT).show();
                //todo undo and redo in contracts page
                return true;
            case R.id.action_redo:
                Toast.makeText(getContext(), "REDO", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContractClicked(Contract contract) {
        Intent detailIntent = new Intent(getContext(), ContractDetailActivity.class);
        detailIntent.putExtra(ContractDetailActivity.EXTRA_CONTRACT_ID, contract.getContractId());
        startActivity(detailIntent);
    }

    private void loadContracts() {
        contractList.clear();
        contractList.addAll(Utils.mergeSort(dbHandler.getAllContracts(), Contract.comparatorId()));
        contractsAdapter.notifyDataSetChanged();
    }
}
