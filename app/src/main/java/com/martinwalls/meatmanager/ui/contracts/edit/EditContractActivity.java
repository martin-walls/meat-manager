package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.DrawableRes;
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public EditContractFragmentViewModel getContractViewModel() {
        return contractViewModel;
    }

    public void setHomeAsUpIcon(@DrawableRes int resId) {
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setHomeAsUpIndicator(resId);
    }

    public void showSelectProductFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_holder, new SelectProductFragment())
                .addToBackStack(SelectProductFragment.class.getSimpleName())
                .commit();
    }

    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }
}
