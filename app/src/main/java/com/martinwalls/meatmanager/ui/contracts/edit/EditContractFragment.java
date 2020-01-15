package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.databinding.FragmentEditContractBinding;
import com.martinwalls.meatmanager.util.Utils;

public class EditContractFragment extends Fragment {

    private final int QUANTITY_MASS_DP = 4;

    private FragmentEditContractBinding binding;

    private EditContractFragmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentEditContractBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof EditContractActivity) {
            viewModel = ((EditContractActivity) getActivity()).getContractViewModel();
        }

        viewModel.getSelectedProductQuantityObservable()
                .observe(getViewLifecycleOwner(), productQuantity -> {
                    if (productQuantity.getProduct() != null) {
                        binding.editTextProduct.setText(productQuantity.getProduct().getProductName());
                    }

                    if (productQuantity.getQuantityMass() == -1) binding.editTextQuantityMass.setText("");
                    else binding.editTextQuantityMass.setText(
                            Utils.getMassDisplayValue(getContext(), productQuantity.getQuantityMass(), QUANTITY_MASS_DP));

                    if (productQuantity.getQuantityBoxes() == -1) binding.editTextQuantityBoxes.setText("");
                    else binding.editTextQuantityBoxes.setText(String.valueOf(productQuantity.getQuantityBoxes()));
                });


    }
}
