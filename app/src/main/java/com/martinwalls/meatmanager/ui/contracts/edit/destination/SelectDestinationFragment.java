package com.martinwalls.meatmanager.ui.contracts.edit.destination;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Location;
import com.martinwalls.meatmanager.databinding.FragmentSearchableListBinding;
import com.martinwalls.meatmanager.ui.contracts.edit.EditContractActivity;
import com.martinwalls.meatmanager.ui.contracts.edit.EditContractViewModel;
import com.martinwalls.meatmanager.ui.locations.edit.NewLocationActivity;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

public class SelectDestinationFragment extends Fragment
        implements DestinationListAdapter.DestinationListAdapterListener {

    private final int REQUEST_NEW_DESTINATION = 1;

    private FragmentSearchableListBinding binding;
    private SelectDestinationViewModel viewModel;
    private EditContractViewModel contractViewModel;

    private DestinationListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchableListBinding.inflate(inflater, container, false);

        viewModel = ViewModelProviders.of(this).get(SelectDestinationViewModel.class);

        adapter = new DestinationListAdapter(this);

        // get list of destinations
        viewModel.getDestinationListObservable().observe(getViewLifecycleOwner(),
                destinations -> adapter.setDestinationList(destinations));

        // initialise list
        binding.recyclerView.setEmptyView(binding.txtNoResults);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // listen for search
        binding.searchBar.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
        });

        binding.btnAddNew.setText(getString(R.string.search_add_new, "destination"));
        binding.btnAddNew.setOnClickListener(v -> addNewDestination());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).setHomeAsUpIcon(R.drawable.ic_back);
            getActivity().setTitle(R.string.contracts_select_destination_title);

            contractViewModel = ((EditContractActivity) getActivity()).getContractViewModel();
        }

        // highlight already selected destination, if there is one
        contractViewModel.getContractObservable().observe(getViewLifecycleOwner(),
                contract -> adapter.setSelectedDestinationId(contract.getDestId()));
    }

    @Override
    public void onDestinationClicked(Location destination) {
        contractViewModel.setDestination(destination);
        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).goBack();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_NEW_DESTINATION) {
            int newId = data.getIntExtra(NewLocationActivity.RESULT_ID, -1);

            viewModel.loadDestinations();
            adapter.setSelectedDestinationId(newId);

            // scroll to new destination in list
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext());
            smoothScroller.setTargetPosition(adapter.getPositionOfItemWithId(newId));
            binding.recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        }
    }

    private void addNewDestination() {
        Intent newDestinationIntent = new Intent(getContext(), NewLocationActivity.class);
        newDestinationIntent.putExtra(NewLocationActivity.EXTRA_LOCATION_TYPE, Location.LocationType.Destination.name());
        startActivityForResult(newDestinationIntent, REQUEST_NEW_DESTINATION);
    }
}
