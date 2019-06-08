package com.martinwalls.nea;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.Nullable;

public class AddNewTextView extends androidx.appcompat.widget.AppCompatTextView {

    private String searchItemType;

    public AddNewTextView(Context context) {
        super(context);
    }

    public AddNewTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AddNewTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
