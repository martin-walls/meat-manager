package com.martinwalls.meatmanager.ui.contracts.edit;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.data.ValidationState;
import com.martinwalls.meatmanager.data.models.Contract;
import com.martinwalls.meatmanager.data.models.Interval;
import com.martinwalls.meatmanager.databinding.FragmentEditContractBinding;
import com.martinwalls.meatmanager.ui.common.adapter.ProductsAddedAdapter;
import com.martinwalls.meatmanager.util.EditTextValidator;
import com.martinwalls.meatmanager.util.Utils;

public class EditContractFragment extends Fragment
        implements RepeatIntervalDialog.RepeatIntervalDialogListener,
        ProductsAddedAdapter.ProductsAddedAdapterListener {

    private final int QUANTITY_MASS_DP = 4;

    private final String SAVED_PRODUCT_TEXT_APPEARANCE = "productTextAppearance";
    private final String SAVED_DESTINATION_TEXT_APPEARANCE = "destinationTextAppearance";
    private final String SAVED_REPEAT_INTERVAL_TEXT_APPEARANCE = "repeatIntervalTextAppearance";
    private final String SAVED_REPEAT_ON_VISIBILITY = "repeatOnVisibility";

    private FragmentEditContractBinding binding;

    private EditContractViewModel viewModel;

    private ProductsAddedAdapter productsAddedAdapter;
    private ArrayAdapter<CharSequence> spnRepeatOnAdapter;

    private Bundle savedState;

    @StyleRes
    private int productTextAppearanceResId = R.style.InputFormTextAppearance_Unselected;
    @StyleRes
    private int destinationTextAppearanceResId = R.style.InputFormTextAppearance_Unselected;
    @StyleRes
    private int repeatIntervalTextAppearanceResId = R.style.InputFormTextAppearance_Unselected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        binding = FragmentEditContractBinding.inflate(inflater, container, false);

        // allow user to select a product from list of all added products when they
        // click the product input field
        binding.txtProduct.setOnClickListener(v -> showSelectProductFragment());

        binding.btnAddProduct.setOnClickListener(v -> commitSelectedProduct());

        productsAddedAdapter = new ProductsAddedAdapter(this, ProductsAddedAdapter.FLAG_SHOW_ALL);
        binding.productsAddedRecyclerView.setAdapter(productsAddedAdapter);
        binding.productsAddedRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // validate quantity inputs as user types
        binding.editTextQuantityMass.addTextChangedListener(
                new EditTextValidator(binding.inputLayoutQuantityMass, binding.editTextQuantityMass,
                        EditTextValidator.VALIDATE_NON_ZERO));

        binding.editTextQuantityBoxes.addTextChangedListener(
                new EditTextValidator(binding.inputLayoutQuantityBoxes, binding.editTextQuantityBoxes,
                        EditTextValidator.VALIDATE_NON_ZERO));

        // allow user to select a destination from list of all added destinations
        // when they click the destination input field
        binding.txtDestination.setOnClickListener(v -> showSelectDestinationFragment());

        // open a dialog to allow user to enter a repeat interval
        binding.txtRepeatInterval.setOnClickListener(v -> showRepeatIntervalDialog());

        // initialise repeat on input spinner with adapter
        spnRepeatOnAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        spnRepeatOnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spnRepeatOn.setAdapter(spnRepeatOnAdapter);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedState != null) {
            savedInstanceState = savedState;
        }
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

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
                        setProductText(product.getProductName());
                        binding.quantityInputsLayout.setVisibility(View.VISIBLE);
                        binding.btnAddProduct.setVisibility(View.VISIBLE);
                    } else {
                        clearProductText();
                        binding.quantityInputsLayout.setVisibility(View.GONE);
                        binding.btnAddProduct.setVisibility(View.GONE);
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

                    if (!TextUtils.isEmpty(contract.getDestName())) {
                        setDestinationText(contract.getDestName());
                    } else {
                        clearDestinationText();
                    }

                    if (contract.getRepeatInterval() != null) {
                        setRepeatIntervalText(contract.getRepeatInterval());
                        binding.textRepeatOn.setText(formatRepeatOnLabelText(contract.getRepeatInterval()));

                        spnRepeatOnAdapter.clear();
                        spnRepeatOnAdapter.addAll(getRepeatOnSpnValues(contract.getRepeatInterval()));
                        spnRepeatOnAdapter.notifyDataSetChanged();
                    }

                    binding.txtReminderValue.setText(String.valueOf(contract.getReminder()));
                    setReminderHintText(contract.getReminder());
                });

        binding.spnRepeatOn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setRepeatOn(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // reminder button listeners
        binding.btnReminderPlus.setOnClickListener(v -> incrementReminder());
        binding.btnReminderMinus.setOnClickListener(v -> decrementReminder());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        saveState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (savedState == null) {
            savedState = new Bundle();
        }
        saveState(savedState);
    }

    private void saveState(@NonNull Bundle outState) {
        outState.putInt(SAVED_PRODUCT_TEXT_APPEARANCE, productTextAppearanceResId);
        outState.putInt(SAVED_DESTINATION_TEXT_APPEARANCE, destinationTextAppearanceResId);
        outState.putInt(SAVED_REPEAT_INTERVAL_TEXT_APPEARANCE, repeatIntervalTextAppearanceResId);

        //todo save quantities row visibility
        outState.putInt(SAVED_REPEAT_ON_VISIBILITY, binding.inputRepeatOn.getVisibility());
    }

    private void restoreState(@NonNull Bundle savedState) {
        binding.inputRepeatOn.setVisibility(savedState.getInt(SAVED_REPEAT_ON_VISIBILITY));

        productTextAppearanceResId = savedState.getInt(SAVED_PRODUCT_TEXT_APPEARANCE);
        binding.txtProduct.setTextAppearance(productTextAppearanceResId);
        destinationTextAppearanceResId = savedState.getInt(SAVED_DESTINATION_TEXT_APPEARANCE);
        binding.txtDestination.setTextAppearance(destinationTextAppearanceResId);
        repeatIntervalTextAppearanceResId = savedState.getInt(SAVED_REPEAT_INTERVAL_TEXT_APPEARANCE);
        binding.txtRepeatInterval.setTextAppearance(repeatIntervalTextAppearanceResId);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_edit_contract, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            commitContract();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSelectProductFragment() {
        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).showSelectProductFragment();
        }
    }

    private boolean validateProductInputField() {
        return viewModel.getSelectedProductObservable().getValue() != null;
    }

    private void setProductText(String text) {
        binding.txtProduct.setText(text);
        productTextAppearanceResId = R.style.InputFormTextAppearance_Selected;
        binding.txtProduct.setTextAppearance(productTextAppearanceResId);
    }

    private void clearProductText() {
        binding.txtProduct.setText(R.string.contracts_input_product);
        if (productTextAppearanceResId != R.style.InputFormTextAppearance_Invalid) {
            productTextAppearanceResId = R.style.InputFormTextAppearance_Unselected;
            binding.txtProduct.setTextAppearance(productTextAppearanceResId);
        }
    }

    private boolean validateQuantityMassInputField() {
        if (TextUtils.isEmpty(binding.editTextQuantityMass.getText())) {
            return false;
        } else {
            return Double.parseDouble(binding.editTextQuantityMass.getText().toString()) != 0;
        }
    }

    private boolean validateQuantityBoxesInputField() {
        // quantity boxes field is allowed to be blank
        if (TextUtils.isEmpty(binding.editTextQuantityBoxes.getText())) {
            return true;
        }
        return Integer.parseInt(binding.editTextQuantityBoxes.getText().toString()) != 0;
    }

    private boolean validateSelectedProductValues() {
        return validateProductInputField()
                & validateQuantityMassInputField()
                & validateQuantityBoxesInputField();
    }

    private boolean commitSelectedProduct() {
        boolean isValid = validateSelectedProductValues();
        if (!isValid) return false;

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
        return true;
    }

    private void showSelectDestinationFragment() {
        if (getActivity() instanceof EditContractActivity) {
            ((EditContractActivity) getActivity()).showSelectDestinationFragment();
        }
    }

    private void setDestinationText(String text) {
        binding.txtDestination.setText(text);
        destinationTextAppearanceResId = R.style.InputFormTextAppearance_Selected;
        binding.txtDestination.setTextAppearance(destinationTextAppearanceResId);
    }

    private void clearDestinationText() {
        binding.txtDestination.setText(R.string.contracts_input_destination);
        if (destinationTextAppearanceResId != R.style.InputFormTextAppearance_Invalid) {
            destinationTextAppearanceResId = R.style.InputFormTextAppearance_Unselected;
            binding.txtDestination.setTextAppearance(destinationTextAppearanceResId);
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

    private void setRepeatIntervalText(Interval interval) {
        binding.txtRepeatInterval.setText(formatRepeatIntervalDisplayText(interval));
        repeatIntervalTextAppearanceResId = R.style.InputFormTextAppearance_Selected;
        binding.txtRepeatInterval.setTextAppearance(repeatIntervalTextAppearanceResId);
    }

    private void clearRepeatIntervalText() {
        binding.txtRepeatInterval.setText(R.string.contracts_input_repeat_interval);
        if (repeatIntervalTextAppearanceResId != R.style.InputFormTextAppearance_Invalid) {
            repeatIntervalTextAppearanceResId = R.style.InputFormTextAppearance_Unselected;
            binding.txtRepeatInterval.setTextAppearance(repeatIntervalTextAppearanceResId);
        }
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

    private void setReminderHintText(int reminderValue) {
        if (reminderValue == 0) {
            binding.txtReminderHint.setText(R.string.contracts_reminder_off_hint);
        } else {
            binding.txtReminderHint.setText(R.string.contracts_reminder_hint);
        }
        binding.txtReminderDaysBefore.setText(getResources().getQuantityString(
                R.plurals.contracts_reminder_days_before, reminderValue));
    }

    private boolean areProductFieldsAllEmpty() {
        return viewModel.getSelectedProductObservable().getValue() == null
                && TextUtils.isEmpty(binding.editTextQuantityMass.getText())
                && TextUtils.isEmpty(binding.editTextQuantityBoxes.getText());
    }

    private void commitContract() {
        //todo add validation for all fields, ie. that they are not empty (use viewModel)

        boolean isValid = true;

        EditContractViewModel.State state = viewModel.getState();
        if (!state.isTotalStateValid()) {
            isValid = false;

            //todo make invalid text appearance persist between switching fragments
            if (state.getProductState() != ValidationState.VALID
                    && state.getProductListState() != ValidationState.VALID) {
                productTextAppearanceResId = R.style.InputFormTextAppearance_Invalid;
                binding.txtProduct.setTextAppearance(productTextAppearanceResId);
            }
            if (state.getDestinationState() != ValidationState.VALID) {
                destinationTextAppearanceResId = R.style.InputFormTextAppearance_Invalid;
                binding.txtDestination.setTextAppearance(destinationTextAppearanceResId);
            }
            if (state.getRepeatIntervalState() != ValidationState.VALID) {
                repeatIntervalTextAppearanceResId = R.style.InputFormTextAppearance_Invalid;
                binding.txtRepeatInterval.setTextAppearance(repeatIntervalTextAppearanceResId);
            }

            Toast.makeText(getContext(), R.string.contract_commit_error_msg, Toast.LENGTH_SHORT).show();
        }

        if (isValid) {
            if (!areProductFieldsAllEmpty()) {
                boolean commitProductSuccess = commitSelectedProduct();
                if (!commitProductSuccess) {
                    if (TextUtils.isEmpty(binding.editTextQuantityMass.getText())) {
                        binding.inputLayoutQuantityMass.setError(getString(R.string.input_error_blank));
                    }
                    return;
                }
            }
            boolean success = viewModel.commitContract();

            if (success) getActivity().finish();
        }
    }
}
