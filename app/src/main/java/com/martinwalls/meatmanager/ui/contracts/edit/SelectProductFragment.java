package com.martinwalls.meatmanager.ui.contracts.edit;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martinwalls.meatmanager.databinding.FragmentSearchableListBinding;

public class SelectProductFragment extends Fragment {

    private FragmentSearchableListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchableListBinding.inflate(inflater, container, false);

        binding.recyclerView.setEmptyView(binding.txtNoResults);

        //todo adapter etc.

        return binding.getRoot();
    }
}
