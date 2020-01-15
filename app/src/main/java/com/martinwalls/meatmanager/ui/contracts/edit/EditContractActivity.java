package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.databinding.ActivityEditContractBinding;

public class EditContractActivity extends AppCompatActivity {

    @Deprecated
    public static final String EXTRA_EDIT_TYPE = "edit_type";
    @Deprecated
    public static final String EXTRA_CONTRACT_ID = "contract_id";

    @Deprecated
    public static final int EDIT_TYPE_NEW = 0;
    @Deprecated
    public static final int EDIT_TYPE_EDIT = 1;

    private ActivityEditContractBinding binding;

    private EditContractFragmentViewModel contractViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditContractBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_holder, new EditContractFragment())
                .commit();

        EditContractFragmentViewModelFactory factory =
                new EditContractFragmentViewModelFactory(getApplication(),
                        EditContractFragmentViewModelFactory.Mode.CREATE_NEW); //todo get real mode

        contractViewModel = ViewModelProviders.of(this, factory)
                .get(EditContractFragmentViewModel.class);
    }

    public EditContractFragmentViewModel getContractViewModel() {
        return contractViewModel;
    }
}
