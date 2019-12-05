package com.martinwalls.meatmanager.ui.contracts.main;

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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.ui.contracts.detail.ContractDetailActivity;
import com.martinwalls.meatmanager.ui.contracts.edit.EditContractActivity;
import com.martinwalls.meatmanager.ui.common.recyclerview.CustomRecyclerView;
import com.martinwalls.meatmanager.ui.common.recyclerview.RecyclerViewDivider;
import com.martinwalls.meatmanager.util.undo.UndoStack;

public class ContractsFragment extends Fragment
        implements ContractsAdapter.ContractsAdapterListener {

    private ContractsViewModel viewModel;

    private ContractsAdapter contractsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.contracts_title);
        View fragmentView = inflater.inflate(R.layout.fragment_contracts, container, false);

        viewModel = ViewModelProviders.of(this).get(ContractsViewModel.class);

        initContractsList(fragmentView);

        viewModel.getContractListObservable().observe(getViewLifecycleOwner(),
                contracts -> contractsAdapter.setContractList(contracts));

        FloatingActionButton fab = fragmentView.findViewById(R.id.fab);
        fab.setOnClickListener(v -> startNewContractActivity());

        return fragmentView;
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
                viewModel.loadContracts();
                return true;
            case R.id.action_redo:
                UndoStack.getInstance().redo(getContext());
                viewModel.loadContracts();
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
     * Initialises the contracts list.
     */
    private void initContractsList(View fragmentView) {
        CustomRecyclerView recyclerView = fragmentView.findViewById(R.id.recycler_view);
        TextView emptyView = fragmentView.findViewById(R.id.empty);
        recyclerView.setEmptyView(emptyView);

        contractsAdapter = new ContractsAdapter(this);
        recyclerView.setAdapter(contractsAdapter);

        RecyclerViewDivider recyclerViewDivider =
                new RecyclerViewDivider(getContext(), R.drawable.divider_thin);
        recyclerView.addItemDecoration(recyclerViewDivider);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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
