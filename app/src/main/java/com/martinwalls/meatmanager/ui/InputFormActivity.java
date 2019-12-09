package com.martinwalls.meatmanager.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.martinwalls.meatmanager.R;
import com.martinwalls.meatmanager.ui.common.AddNewTextView;
import com.martinwalls.meatmanager.data.models.SearchItem;
import com.martinwalls.meatmanager.ui.common.adapter.SearchItemAdapter;
import com.martinwalls.meatmanager.util.SimpleTextWatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Abstract Activity class that provides methods for managing search fields
 * in input forms throughout the app. This handles opening and closing search
 * views.
 */
public abstract class InputFormActivity extends AppCompatActivity
        implements SearchItemAdapter.SearchItemAdapterListener {

    private SearchItemViewModel viewModel;

    private SearchItemAdapter searchItemAdapter;

    private HashMap<String, Integer> viewsToHide = new HashMap<>();
    @Deprecated
    private List<SearchItem> searchItemList = new ArrayList<>();
    private String currentSearchType;

    private ViewGroup rootView;
    private AddNewTextView addNewView;
    private LinearLayout searchResultsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        searchItemAdapter = new SearchItemAdapter(this);
    }


    protected final <T extends SearchItemViewModel> void setSearchItemViewModel(
            Class<T> viewModelClass) {
        viewModel = ViewModelProviders.of(this).get(viewModelClass);

        viewModel.getSearchItemList().observe(this,
                searchItems -> searchItemAdapter.setSearchItems(searchItems));
    }

    protected final void setViewModel(SearchItemViewModel viewModel) {
        this.viewModel = viewModel;

        viewModel.getSearchItemList().observe(this,
                searchItems -> searchItemAdapter.setSearchItems(searchItems));
    }

    /**
     * This is called when a search result is clicked when the search view is
     * open for a particular input field.
     */
    @Override
    public abstract void onSearchItemSelected(SearchItem item, String searchItemType);

    /**
     * This should be implemented to store a list of search items in
     * {@link #searchItemList}. It is called whenever the search view is
     * opened for a particular field.
     */
    @CallSuper
    @Deprecated
    protected void loadSearchItems(String searchType) {
        searchItemList.clear();
    }

    /**
     * This is called when the "Add new item" button is clicked from a search
     * view. It should be overridden to provide an appropriate implementation
     * for adding a new item, either with a dialog or a separate activity.
     */
    protected abstract void addNewItemFromSearch(String searchType);

    /**
     * Stores a reference to the "Add new item" button in search view.
     */
    protected final void setAddNewView(AddNewTextView view) {
        addNewView = view;
        addNewView.setOnClickListener(v ->
                addNewItemFromSearch(addNewView.getSearchItemType()));
    }

    @Deprecated
    protected final void setAddNewView(@IdRes int id) {
        addNewView = findViewById(id);
        addNewView.setOnClickListener(v ->
                addNewItemFromSearch(addNewView.getSearchItemType()));
    }

    /**
     * Hides the "Add new item" button.
     */
    protected final void hideAddNewView() {
        addNewView.setVisibility(View.GONE);
    }

    /**
     * Stores a reference to the root layout of the activity.
     */
    protected final void setRootView(ViewGroup viewGroup) {
        rootView = viewGroup;
    }

    @Deprecated
    protected final void setRootView(@IdRes int id) {
        rootView = findViewById(id);
    }

    /**
     * Stores a reference to the search results layout.
     */
    protected final void setSearchResultsLayout(LinearLayout view) {
        searchResultsLayout = view;
    }

    @Deprecated
    protected final void setSearchResultsLayout(@IdRes int id) {
        searchResultsLayout = findViewById(id);
    }

    /**
     * Sets the {@link SearchItemAdapter} for the search results RecyclerView.
     */
    protected final void setSearchItemAdapter(SearchItemAdapter adapter) {
        this.searchItemAdapter = adapter;
    }

    /**
     * Returns the {@link SearchItemAdapter} associated with the search
     * results RecyclerView.
     */
    protected final SearchItemAdapter getSearchItemAdapter() {
        return searchItemAdapter;
    }

    /**
     * Returns the list of search items shown in the search view.
     */
    @Deprecated
    protected final List<SearchItem> getSearchItemList() {
        return searchItemList;
    }

    /**
     * Adds a {@link SearchItem} to the search items list to be shown in
     * the search results layout.
     */
    @Deprecated
    protected final void addSearchItemToList(SearchItem item) {
        searchItemList.add(item);
    }

    /**
     * Returns the current search type of the search view.
     */
    protected final String getCurrentSearchType() {
        return currentSearchType;
    }

    /**
     * Sets the search type of the search view that is currently open / being opened.
     */
    protected final void setCurrentSearchType(String currentSearchType) {
        this.currentSearchType = currentSearchType;
        searchItemAdapter.setSearchItemType(currentSearchType);
    }

    /**
     * Adds a reference to a View that should be hidden when the search view
     * is open. Typically this should be any View that is part of the input
     * form.
     *
     * @param name  Name of the View so it can be referred to later.
     * @param resId The ID of the View to hide.
     */
    protected final void addViewToHide(String name, @IdRes int resId) {
        viewsToHide.put(name, resId);
    }

    /**
     * Hides the soft keyboard if it is showing.
     */
    protected final void hideKeyboard() {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
        }
    }

    /**
     * Sets up listeners for the input field.
     * <ul>
     *     <li>When the View is focused, it opens the search view for that field.
     *     <li>When the clear icon is clicked, it clears the input field and
     *         closes the search layout.
     *     <li>When the text in the field changes, it filters the search results by
     *         the text entered.
     * </ul>
     */
    protected final void setListeners(String name,
                                TextInputLayout inputLayout, TextInputEditText editText) {
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

    /**
     * Opens the search view for a particular input field. Animates the layout
     * changes and hides all views in {@link #viewsToHide} apart from the
     * input field for this search.
     *
     * @param inputName Name of the input field
     */
    @CallSuper
    protected void openSearch(String inputName) {
        Transition moveTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.search_open);
        TransitionManager.beginDelayedTransition(rootView, moveTransition);
        for (int view : viewsToHide.values()) {
            if (view != viewsToHide.get(inputName)) {
                findViewById(view).setVisibility(View.GONE);
            }
        }

//        loadSearchItems(inputName);
        viewModel.loadSearchItems(inputName);

        currentSearchType = inputName;
        searchItemAdapter.setSearchItemType(currentSearchType);

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

    /**
     * Closes the search view. Animates the layout changes and shows all views
     * in {@link #viewsToHide} again to return the input form to its original
     * layout.
     */
    @CallSuper
    protected void cancelSearch() {
        Transition closeTransition = TransitionInflater.from(this)
                .inflateTransition(R.transition.search_close);
        TransitionManager.beginDelayedTransition(rootView, closeTransition);
        for (int view : viewsToHide.values()) {
            View viewToShow = findViewById(view);
            viewToShow.setVisibility(View.VISIBLE);
        }

        searchResultsLayout.setVisibility(View.GONE);

        hideKeyboard();
    }
}