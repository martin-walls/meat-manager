package com.martinwalls.nea;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.nea.components.AddNewTextView;
import com.martinwalls.nea.models.SearchItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public abstract class InputFormActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener {

    private SearchItemAdapter searchItemAdapter; //todo setters?
    private ViewGroup rootView;

    private HashMap<String, Integer> inputViews = new HashMap<>();
    private List<SearchItem> searchItemList = new ArrayList<>();
    private String currentSearchType;

    private AddNewTextView addNewView;
    private LinearLayout searchResultsLayout;

    @Override
    public abstract void onSearchItemSelected(SearchItem item, String searchItemType);

    protected abstract void loadSearchItems(String searchType);

    protected abstract void addNewItem(String searchType);

    protected void setAddNewView(@IdRes int resId) {
        addNewView = findViewById(resId);
        addNewView.setOnClickListener(v -> addNewItem(addNewView.getSearchItemType()));
    }

    protected void setRootView(@IdRes int resId) {
        rootView = findViewById(resId);
    }

    protected void setSearchResultsLayout(@IdRes int resId) {
        searchResultsLayout = findViewById(resId);
    }

    protected void setSearchItemAdapter(SearchItemAdapter adapter) {
        this.searchItemAdapter = adapter;
    }

    protected SearchItemAdapter getSearchItemAdapter() {
        return searchItemAdapter;
    }

    protected List<SearchItem> getSearchItemList() {
        return searchItemList;
    }

    protected void addSearchItemToList(SearchItem item) {
        searchItemList.add(item);
    }

    protected String getCurrentSearchType() {
        return currentSearchType;
    }

    protected void setCurrentSearchType(String currentSearchType) {
        this.currentSearchType = currentSearchType;
    }

    protected void addInputView(String inputName, @IdRes int resId) {
        inputViews.put(inputName, resId);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    protected void setListeners(String name, TextInputLayout inputLayout, TextInputEditText editText) {
        inputLayout.setEndIconVisible(false);

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                inputLayout.setEndIconVisible(true);
                openSearch(name);
            }
        });

        inputLayout.setEndIconOnClickListener(v -> {
            editText.setText("");
            editText.clearFocus();
            inputLayout.setEndIconVisible(false);
            cancelSearch();
        });

        editText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchItemAdapter.getFilter().filter(s);
            }
        });
    }

    protected void openSearch(String inputName) {
        Transition moveTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_open);
        TransitionManager.beginDelayedTransition(rootView, moveTransition);
        for (int view : inputViews.values()) {
            if (view != inputViews.get(inputName)) {
                findViewById(view).setVisibility(GONE);
            }
        }

        searchItemList.clear();
        loadSearchItems(inputName);
        currentSearchType = inputName;
        searchItemAdapter.setSearchItemType(currentSearchType);
        searchItemAdapter.notifyDataSetChanged();

        // filter data at start in case text already entered
        TextInputEditText editText = (TextInputEditText) getCurrentFocus();
        searchItemAdapter.getFilter().filter(editText.getText());

        addNewView.setVisibility(View.VISIBLE);
        addNewView.setText(getString(R.string.search_add_new, inputName.toLowerCase()));
        addNewView.setSearchItemType(inputName);

        searchResultsLayout.setAlpha(0f);
        searchResultsLayout.setVisibility(View.VISIBLE);

        searchResultsLayout.animate()
                .alpha(1f)
                .setStartDelay(getResources().getInteger(R.integer.search_results_fade_delay))
                .setDuration(getResources().getInteger(R.integer.search_results_fade_duration))
                .setListener(null);
    }

    protected void cancelSearch() {
        Transition closeTransition = TransitionInflater.from(this).inflateTransition(R.transition.search_close);
        TransitionManager.beginDelayedTransition(rootView, closeTransition);
        for (int view : inputViews.values()) {
            View viewToShow = findViewById(view);
            viewToShow.setVisibility(View.VISIBLE);
        }

        searchResultsLayout.setVisibility(GONE);

        hideKeyboard();
    }
}