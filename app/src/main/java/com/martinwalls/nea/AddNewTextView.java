package com.martinwalls.nea;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class AddNewTextView extends AppCompatTextView {

    private String searchItemType;

    public AddNewTextView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AddNewTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AddNewTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AddNewTextView, defStyleAttr, 0);
        searchItemType = a.getString(R.styleable.AddNewTextView_searchItemType);
        a.recycle();
    }

    public void setSearchItemType(String searchItemType) {
        this.searchItemType = searchItemType;
    }

    public String getSearchItemType() {
        return searchItemType;
    }
}
