package com.martinwalls.nea.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.martinwalls.nea.data.models.Conversion;
import com.martinwalls.nea.data.models.Currency;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class ExchangeDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "exchangeDB.db";
    private static final int DATABASE_VERSION = 3;

    private final class ConversionsTable {
        static final String TABLE_NAME = "Conversions";
        static final String ID = "ConversionId";
        static final String TIMESTAMP = "Timestamp";
        static final String PRIMARY_CURRENCY = "PrimaryCurrency";
        static final String PRIMARY_VALUE = "PrimaryValue";
        static final String SECONDARY_CURRENCY = "SecondaryCurrency";
        static final String SECONDARY_VALUE = "SecondaryValue";
    }

    private final class CurrenciesTable {
        static final String TABLE_NAME = "Currencies";
        static final String CURRENCY_CODE = "CurrencyCode";
        static final String CURRENCY_NAME = "CurrencyName";
        static final String FAVOURITE = "IsFavourite";
    }

    public ExchangeDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCurrenciesTableQuery = "CREATE TABLE IF NOT EXISTS "
                + CurrenciesTable.TABLE_NAME + " ("
                + CurrenciesTable.CURRENCY_CODE + " TEXT PRIMARY KEY, "
                + CurrenciesTable.CURRENCY_NAME + " TEXT NOT NULL, "
                + CurrenciesTable.FAVOURITE + " INTEGER DEFAULT 0 )";
        db.execSQL(createCurrenciesTableQuery);

        String createConversionsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + ConversionsTable.TABLE_NAME + " ("
                + ConversionsTable.ID + " INTEGER PRIMARY KEY, "
                + ConversionsTable.TIMESTAMP + " INTEGER NOT NULL, "
                + ConversionsTable.PRIMARY_CURRENCY + " TEXT NOT NULL, "
                + ConversionsTable.PRIMARY_VALUE + " REAL NOT NULL, "
                + ConversionsTable.SECONDARY_CURRENCY + " TEXT NOT NULL, "
                + ConversionsTable.SECONDARY_VALUE + " REAL NOT NULL, "
                + "FOREIGN KEY (" + ConversionsTable.PRIMARY_CURRENCY + ") REFERENCES "
                + CurrenciesTable.TABLE_NAME + "(" + CurrenciesTable.CURRENCY_CODE + "), "
                + "FOREIGN KEY (" + ConversionsTable.SECONDARY_CURRENCY + ") REFERENCES "
                + CurrenciesTable.TABLE_NAME + "(" + CurrenciesTable.CURRENCY_CODE + ") )";
        db.execSQL(createConversionsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    public List<Conversion> getAllConversions() {
        List<Conversion> conversionsResultList = new ArrayList<>();
        String query = "SELECT * FROM " + ConversionsTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Conversion conversion = new Conversion();
            conversion.setConversionId(cursor.getInt(cursor.getColumnIndexOrThrow(ConversionsTable.ID)));
            conversion.setTimestamp(cursor.getInt(cursor.getColumnIndexOrThrow(ConversionsTable.TIMESTAMP)));
            conversion.setPrimaryCurrency(getCurrency(cursor.getString(
                            cursor.getColumnIndexOrThrow(ConversionsTable.PRIMARY_CURRENCY))));
            conversion.setPrimaryValue(cursor.getDouble(cursor.getColumnIndexOrThrow(ConversionsTable.PRIMARY_VALUE)));
            conversion.setSecondaryCurrency(getCurrency(cursor.getString(
                    cursor.getColumnIndexOrThrow(ConversionsTable.SECONDARY_CURRENCY))));
            conversion.setSecondaryValue(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(ConversionsTable.SECONDARY_VALUE)));
            conversionsResultList.add(conversion);
        }
        cursor.close();
        db.close();
        return conversionsResultList;
    }

    public boolean addConversion(Conversion conversion) {
        ContentValues values = new ContentValues();
        values.put(ConversionsTable.PRIMARY_CURRENCY, conversion.getPrimaryCurrency().getCode());
        values.put(ConversionsTable.PRIMARY_VALUE, conversion.getPrimaryValue());
        values.put(ConversionsTable.SECONDARY_CURRENCY, conversion.getSecondaryCurrency().getCode());
        values.put(ConversionsTable.SECONDARY_VALUE, conversion.getSecondaryValue());
        values.put(ConversionsTable.TIMESTAMP, conversion.getTimestamp());

        SQLiteDatabase db = this.getWritableDatabase();
        boolean success = db.insert(ConversionsTable.TABLE_NAME, null, values) != -1;
        db.close();
        return success;
    }

    public Currency getCurrency(String currencyCode) {
        Currency currencyResult = new Currency();
        String query = "SELECT * FROM " + CurrenciesTable.TABLE_NAME
                + " WHERE " + CurrenciesTable.CURRENCY_CODE + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{currencyCode});
        if (cursor.moveToFirst()) {
            currencyResult.setCode(cursor.getString(cursor.getColumnIndexOrThrow(CurrenciesTable.CURRENCY_CODE)));
            currencyResult.setName(cursor.getString(cursor.getColumnIndexOrThrow(CurrenciesTable.CURRENCY_NAME)));
            currencyResult.setFavourite(cursor.getInt(cursor.getColumnIndexOrThrow(CurrenciesTable.FAVOURITE)) == 1);
        }
        cursor.close();
        db.close();
        return currencyResult;
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencyResultList = new ArrayList<>();
        String query = "SELECT * FROM " + CurrenciesTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Currency currency = new Currency();
            currency.setCode(cursor.getString(cursor.getColumnIndexOrThrow(CurrenciesTable.CURRENCY_CODE)));
            currency.setName(cursor.getString(cursor.getColumnIndexOrThrow(CurrenciesTable.CURRENCY_NAME)));
            currency.setFavourite(cursor.getInt(cursor.getColumnIndexOrThrow(CurrenciesTable.FAVOURITE)) == 1);
            currencyResultList.add(currency);
        }
        cursor.close();
        db.close();
        return currencyResultList;
    }

    public List<Currency> getFavCurrencies() {
        List<Currency> currencyResultList = new ArrayList<>();
        String query = "SELECT * FROM " + CurrenciesTable.TABLE_NAME
                + " WHERE " + CurrenciesTable.FAVOURITE + "=1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Currency currency = new Currency();
            currency.setCode(cursor.getString(cursor.getColumnIndexOrThrow(CurrenciesTable.CURRENCY_CODE)));
            currency.setName(cursor.getString(cursor.getColumnIndexOrThrow(CurrenciesTable.CURRENCY_NAME)));
            currency.setFavourite(cursor.getInt(cursor.getColumnIndexOrThrow(CurrenciesTable.FAVOURITE)) == 1);
            currencyResultList.add(currency);
        }
        cursor.close();
        db.close();
        return currencyResultList;
    }

    public int getCurrencyCount() {
        String query = "SELECT * FROM " + CurrenciesTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public void addAllCurrencies(List<Currency> currencyList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Currency currency : currencyList) {
            ContentValues values = new ContentValues();
            values.put(CurrenciesTable.CURRENCY_CODE, currency.getCode());
            values.put(CurrenciesTable.CURRENCY_NAME, currency.getName());
            values.put(CurrenciesTable.FAVOURITE, currency.isFavourite() ? 1 : 0);
            db.insert(CurrenciesTable.TABLE_NAME, null, values);
        }
        db.close();
    }

    public void setCurrencyFavourite(String currencyCode, boolean isFav) {
        ContentValues values = new ContentValues();
        values.put(CurrenciesTable.FAVOURITE, isFav ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(CurrenciesTable.TABLE_NAME, values,
                CurrenciesTable.CURRENCY_CODE + "=?", new String[]{currencyCode});
        db.close();
    }
}
