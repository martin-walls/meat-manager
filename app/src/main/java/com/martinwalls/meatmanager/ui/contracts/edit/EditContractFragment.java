package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.databinding.FragmentEditContractBinding;
import com.martinwalls.meatmanager.util.Utils;

public class EditContractFragment extends Fragment {

    private final int QUANTITY_MASS_DP = 4;

    private FragmentEditContractBinding binding;

    private EditContractFragmentViewModel viewModel;

    private ArrayAdapter<CharSequence> spnRepeatOnAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        binding = FragmentEditContractBinding.inflate(inflater, container, false);

        // initialise repeat on input spinner with adapter
        spnRepeatOnAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        spnRepeatOnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnRepeatOn.setAdapter(spnRepeatOnAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() instanceof EditContractActivity) {
            viewModel = ((EditContractActivity) getActivity()).getContractViewModel();
        }

        // update product and quantity inputs when values update
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

        // update fields when contract updates:
        //   - destination
        //   - repeat interval
        //   - repeat on (spinner)
        //   - reminder
        viewModel.getContractObservable()
                .observe(getViewLifecycleOwner(), contract -> {
                    binding.editTextDestination.setText(contract.getDestName());

                    if (contract.getRepeatInterval() != null) {
                        binding.editTextRepeatInterval.setText(
                                formatRepeatIntervalDisplayText(contract.getRepeatInterval()));
                        binding.textRepeatOn.setText(formatRepeatOnLabelText(contract.getRepeatInterval()));

                        spnRepeatOnAdapter.clear();
                        spnRepeatOnAdapter.addAll(getRepeatOnSpnValues(contract.getRepeatInterval()));
                        spnRepeatOnAdapter.notifyDataSetChanged();
                    }

                    binding.editTextReminder.setText(String.valueOf(contract.getReminder()));
                });

        // reminder button listeners
        binding.btnReminderPlus.setOnClickListener(v -> incrementReminder());
        binding.btnReminderMinus.setOnClickListener(v -> decrementReminder());
    }

    /**
     * Returns a string representation of an interval.
     */
    @NonNull
    private String formatRepeatIntervalDisplayText(@NonNull Interval interval) {
        if (interval.getValue() == 1) {
            return getString(
                    R.string.contracts_repeat_interval_display_one,
                    interval.getUnit().name().toLowerCase());
        } else {
            return getString(
                    R.string.contracts_repeat_interval_display_multiple,
                    interval.getValue(), interval.getUnit().name().toLowerCase());
        }
    }

    @NonNull
    private String formatRepeatOnLabelText(@NonNull Interval interval) {
        @StringRes int stringRes;
        if (interval.getUnit() == Interval.TimeUnit.WEEK) {
            if (interval.getValue() == 1) {
                stringRes = R.string.contracts_repeat_on_week;
            } else if (interval.getValue() == 2) {
                stringRes = R.string.contracts_repeat_on_two_week;
            } else {
                stringRes = R.string.contracts_repeat_on_default;
            }
        } else if (interval.getUnit() == Interval.TimeUnit.MONTH) {
            if (interval.getValue() == 1) {
                stringRes = R.string.contracts_repeat_on_month;
            } else {
                stringRes = R.string.contracts_repeat_on_default;
            }
        } else {
            stringRes = R.string.contracts_repeat_on_default;
        }
        return getString(stringRes);
    }

    @NonNull
    private String[] getRepeatOnSpnValues(@NonNull Interval interval) {
        if (interval.getUnit() == Interval.TimeUnit.WEEK) {
            return getResources().getStringArray(R.array.weekdays);
        } else {
            return getResources().getStringArray(R.array.month_days);
        }
    }

    private void incrementReminder() {
        viewModel.updateReminderBy(1);
    }

    private void decrementReminder() {
        viewModel.updateReminderBy(-1);
    }
}
