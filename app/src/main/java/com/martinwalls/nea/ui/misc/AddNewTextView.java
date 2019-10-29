package com.martinwalls.nea.ui.misc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.martinwalls.nea.R;

public class AddNewTextView extends AppCompatTextView {

    private String searchItemType;

    public AddNewTextView(Context context) {
        this(context, null);
    }

    public AddNewTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
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
