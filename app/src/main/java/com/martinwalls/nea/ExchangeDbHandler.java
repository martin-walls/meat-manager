package com.martinwalls.nea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExchangeDbHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "exchangeDB.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    //region database constants
    private enum conversionsTable {
        TABLE_NAME("Conversions"),
        COL_CONVERSION_ID("ConversionId"),
        COL_TIMESTAMP("Timestamp"),
        COL_PRIMARY_CURRENCY("PrimaryCurrency"),
        COL_PRIMARY_VALUE("PrimaryValue"),
        COL_SECONDARY_CURRENCY("SecondaryCurrency"),
        COL_SECONDARY_VALUE("SecondaryValue");

        private String name;
        conversionsTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum favouritesTable {
        TABLE_NAME("Favourites"),
        COL_CURRENCY_CODE("CurrencyCode");

        private String name;
        favouritesTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    //endregion database constants

    public ExchangeDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
