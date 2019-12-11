package com.martinwalls.meatmanager.ui.tutorial;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.martinwalls.meatmanager.R;

import java.util.List;

public class TutorialMeatTypesAdapter
        extends RecyclerView.Adapter<TutorialMeatTypesAdapter.ViewHolder> {

    private List<String> meatTypes;

    private TutorialMeatTypesAdapterListener listener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private CheckBox checkbox;

        ViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            checkbox = view.findViewById(R.id.checkbox);

            name.setOnClickListener(v -> checkbox.setChecked(!checkbox.isChecked()));

            checkbox.setOnCheckedChangeListener((buttonView, isChecked) ->
                    listener.onMeatTypeChecked(
                            meatTypes.get(getAdapterPosition()), isChecked));
        }
    }

    TutorialMeatTypesAdapter(List<String> meatTypes,
                             TutorialMeatTypesAdapterListener listener) {
        this.meatTypes = meatTypes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutorial_meat_type, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(meatTypes.get(position));
    }

    @Override
    public int getItemCount() {
        return meatTypes.size();
    }

    /**
     * Interface to handle clicks on meat types list items.
     */
    public interface TutorialMeatTypesAdapterListener {
        /**
         * This is called when the user checks/unchecks an item in the list.
         */
        void onMeatTypeChecked(String name, boolean isChecked);
    }
}
