package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.databinding.FragmentEditContractBinding;
import com.martinwalls.meatmanager.ui.common.adapter.ProductsAddedAdapter;
import com.martinwalls.meatmanager.util.Utils;

public class EditContractFragment extends Fragment
        implements RepeatIntervalDialog.RepeatIntervalDialogListener,
        ProductsAddedAdapter.ProductsAddedAdapterListener {

    private final int QUANTITY_MASS_DP = 4;

    private FragmentEditContractBinding binding;

    private EditContractFragmentViewModel viewModel;

    private ProductsAddedAdapter productsAddedAdapter;
    private ArrayAdapter<CharSequence> spnRepeatOnAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentEditContractBinding.inflate(inflater, container, false);

        // allow user to select a product from list of all added products when they
        // click the product input field
        binding.editTextProduct.setOnClickListener(v -> showSelectProductFragment());

        binding.btnAddProduct.setOnClickListener(v -> commitSelectedProduct());

        productsAddedAdapter = new ProductsAddedAdapter(this, ProductsAddedAdapter.FLAG_SHOW_ALL);
        binding.productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        binding.productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // allow user to select a destination from list of all added destinations
        // when they click the destination input field
        binding.editTextDestination.setOnClickListener(v -> showSelectDestinationFragment());

        // open a dialog to allow user to enter a repeat interval
        binding.editTextRepeatInterval.setOnClickListener(v -> showRepeatIntervalDialog());

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
            ((EditContractActivity) getActivity()).setHomeAsUpIcon(R.drawable.ic_cancel);
        }

        if (viewModel.isNewContract()) {
            getActivity().setTitle(R.string.contract_new_title);
        } else {
            getActivity().setTitle(R.string.contract_edit_title);
        }

        // update product input field when user selects a different product, or
        // the current product is committed.
        viewModel.getSelectedProductObservable()
                .observe(getViewLifecycleOwner(), product -> {
                    if (product != null) {
                        binding.editTextProduct.setText(product.getProductName());
                    } else {
                        binding.editTextProduct.setText("");
                    }
                });

        // update fields when contract updates:
        //   - destination
        //   - repeat interval
        //   - repeat on (spinner)
        //   - reminder
        viewModel.getContractObservable()
                .observe(getViewLifecycleOwner(), contract -> {
                    productsAddedAdapter.setProductList(contract.getProductList());

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

    private void showSelectProductFragment() {
        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).showSelectProductFragment();
        }
    }

    private boolean validateProductInputField() {
        if (viewModel.getSelectedProductObservable().getValue() == null) {
            binding.inputLayoutProduct.setError(getString(R.string.input_error_blank));
            return false;
        } else {
            binding.inputLayoutProduct.setError(null);
            return true;
        }
    }

    private boolean validateQuantityMassInputField() {
        if (TextUtils.isEmpty(binding.editTextQuantityMass.getText())) {
            binding.inputLayoutQuantityMass.setError(getString(R.string.input_error_blank));
            return false;
        } else if (Double.parseDouble(binding.editTextQuantityMass.getText().toString()) == 0) {
            binding.inputLayoutQuantityMass.setError(getString(R.string.input_error_non_zero));
            return false;
        } else {
            binding.inputLayoutQuantityMass.setError(null);
            return true;
        }
    }

    private boolean validateQuantityBoxesInputField() {
        // quantity boxes field is allowed to be blank
        if (TextUtils.isEmpty(binding.editTextQuantityBoxes.getText())) {
            binding.inputLayoutQuantityMass.setError(null);
            return true;
        }
        if (Integer.parseInt(binding.editTextQuantityBoxes.getText().toString()) == 0) {
            binding.inputLayoutQuantityBoxes.setError(getString(R.string.input_error_non_zero));
            return false;
        } else {
            binding.inputLayoutQuantityBoxes.setError(null);
            return true;
        }
    }

    private boolean validateSelectedProductValues() {
        return validateProductInputField()
                & validateQuantityMassInputField()
                & validateQuantityBoxesInputField();
    }

    //todo validation
    private void commitSelectedProduct() {
        boolean isValid = validateSelectedProductValues();
        if (!isValid) return;

        double mass = Utils.getKgsFromCurrentMassUnit(getContext(),
                Double.parseDouble(binding.editTextQuantityMass.getText().toString()));

        int numBoxes = -1;
        if (!TextUtils.isEmpty(binding.editTextQuantityBoxes.getText())) {
            numBoxes = Integer.parseInt(binding.editTextQuantityBoxes.getText().toString());
        }

        viewModel.commitSelectedProduct(mass, numBoxes);
        binding.editTextQuantityMass.setText("");
        binding.editTextQuantityMass.clearFocus();
        binding.editTextQuantityBoxes.setText("");
        binding.editTextQuantityBoxes.clearFocus();
    }

    private void showSelectDestinationFragment() {
        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).showSelectDestinationFragment();
        }
    }

    private void showRepeatIntervalDialog() {
        DialogFragment dialog = new RepeatIntervalDialog(this);
        Bundle args = new Bundle();
        Contract contract = viewModel.getContractObservable().getValue();
        if (contract != null) {
            Interval currentRepeatInterval = contract.getRepeatInterval();
            //todo just pass interval, ie. move this logic into the dialog
            if (currentRepeatInterval != null) {
                if (currentRepeatInterval.hasValues(1, Interval.TimeUnit.WEEK)) {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                            RepeatIntervalDialog.OPTION_WEEK);
                } else if (currentRepeatInterval.hasValues(2, Interval.TimeUnit.WEEK)) {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                            RepeatIntervalDialog.OPTION_TWO_WEEK);
                } else if (currentRepeatInterval.hasValues(1, Interval.TimeUnit.MONTH)) {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                            RepeatIntervalDialog.OPTION_MONTH);
                } else {
                    args.putInt(RepeatIntervalDialog.EXTRA_SELECTED,
                            RepeatIntervalDialog.OPTION_CUSTOM);
                    args.putSerializable(RepeatIntervalDialog.EXTRA_CUSTOM_INTERVAL, currentRepeatInterval);
                }
            }
        }
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "select_repeat_interval");
    }

    @Override
    public void onRepeatIntervalSelected(Interval interval) {
        viewModel.setRepeatInterval(interval);
        binding.inputRepeatOn.setVisibility(View.VISIBLE);
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
