package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.databinding.FragmentSearchableListBinding;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

public class SelectProductFragment extends Fragment
        implements ProductListAdapter.ProductListAdapterListener {

    private FragmentSearchableListBinding binding;
    private SelectProductViewModel viewModel;

    private ProductListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentSearchableListBinding.inflate(inflater, container, false);

        viewModel = ViewModelProviders.of(this).get(SelectProductViewModel.class);

        adapter = new ProductListAdapter(this);

        viewModel.getProductListObservable().observe(getViewLifecycleOwner(),
                products -> adapter.setProductList(products));

        binding.recyclerView.setEmptyView(binding.txtNoResults);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.searchBar.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).setHomeAsUpIcon(R.drawable.ic_back);
        }
    }

    @Override
    public void onProductClicked(Product product) {
        Toast.makeText(getContext(), product.getProductName(), Toast.LENGTH_SHORT).show();
    }
}
