package com.martinwalls.nea.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExchangeDbHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "exchangeDB.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    //region database constants
    // table names
    private final String TABLE_CONVERSIONS = "Conversions";
    private final String TABLE_FAVOURITES = "Favourites";

    // cols for conversions table
    private final String CONVERSIONS_ID = "ConversionId";
    private final String CONVERSIONS_TIMESTAMP = "Timestamp";
    private final String CONVERSION_PRIMARY_CURRENCY = "PrimaryCurrency";
    private final String CONVERSION_PRIMARY_VALUE = "PrimaryValue";
    private final String CONVERSION_SECONDARY_CURRENCY = "SecondaryCurrency";
    private final String CONVERSION_SECONDARY_VALUE = "SecondaryValue";

    // cols for favourites table
    private final String FAVOURITES_CURRENCY = "CurrencyCode";
    //endregion database constants

    public ExchangeDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createConversionsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_CONVERSIONS + " ("
                + CONVERSIONS_ID + " INTEGER PRIMARY KEY, "
                + CONVERSIONS_TIMESTAMP + " INTEGER NOT NULL, "
                + CONVERSION_PRIMARY_CURRENCY + " TEXT NOT NULL, "
                + CONVERSION_PRIMARY_VALUE + " REAL NOT NULL, "
                + CONVERSION_SECONDARY_CURRENCY + " TEXT NOT NULL, "
                + CONVERSION_SECONDARY_VALUE + " REAL NOT NULL )";
        db.execSQL(createConversionsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
