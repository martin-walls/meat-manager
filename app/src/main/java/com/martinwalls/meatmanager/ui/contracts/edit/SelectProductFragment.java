package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Product;
import com.martinwalls.meatmanager.databinding.FragmentSearchableListBinding;
import com.martinwalls.meatmanager.ui.products.AddNewProductDialog;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

public class SelectProductFragment extends Fragment
        implements ProductListAdapter.ProductListAdapterListener,
        AddNewProductDialog.AddNewProductListener {

    private FragmentSearchableListBinding binding;
    private SelectProductViewModel viewModel;
    private EditContractFragmentViewModel contractViewModel;

    private ProductListAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchableListBinding.inflate(inflater, container, false);

        viewModel = ViewModelProviders.of(this).get(SelectProductViewModel.class);

        adapter = new ProductListAdapter(this);

        // get list of products
        viewModel.getProductListObservable().observe(getViewLifecycleOwner(),
                products -> adapter.setProductList(products));

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

        binding.btnAddNew.setText(getString(R.string.search_add_new, "product"));
        binding.btnAddNew.setOnClickListener(v -> addNewProduct());

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).setHomeAsUpIcon(R.drawable.ic_back);
            getActivity().setTitle(R.string.contracts_select_product_title);

            contractViewModel = ((EditContractActivity) getActivity()).getContractViewModel();
        }

        // highlight already selected product, if there is one
        contractViewModel.getSelectedProductQuantityObservable().observe(getViewLifecycleOwner(),
                productQuantity -> adapter.setSelectedProduct(productQuantity.getProduct()));
    }

    @Override
    public void onProductClicked(Product product) {
        contractViewModel.setSelectedProduct(product);
        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).goBack();
        }
    }

    @Override
    public void onAddNewProductDoneAction(Product newProduct) {
        int newId = viewModel.addNewProduct(newProduct);
        if (newId == -1) {
            Toast.makeText(getContext(), R.string.db_error_insert_product, Toast.LENGTH_SHORT).show();
            return;
        }
        newProduct.setProductId(newId);
        adapter.setSelectedProduct(newProduct);

        // scroll to make new product visible in list
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext());
        smoothScroller.setTargetPosition(adapter.getPositionOfItemWithId(newId));
        binding.recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
    }

    private void addNewProduct() {
        // show add new product dialog
        DialogFragment dialog = new AddNewProductDialog(this);
        dialog.show(getFragmentManager(), "add_new_product");
    }
}
