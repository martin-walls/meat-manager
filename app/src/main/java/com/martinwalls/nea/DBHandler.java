package com.martinwalls.nea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stockDB.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    //region database constants
    // table names
    private static final String TABLE_PRODUCTS = "Products";
    private static final String TABLE_STOCK = "Stock";
    private static final String TABLE_ORDERS = "Orders";
    private static final String TABLE_ORDER_PRODUCTS = "OrderProducts";
    private static final String TABLE_CONTRACTS = "Contracts";
    private static final String TABLE_CONTRACT_PRODUCTS = "ContractProducts";
    private static final String TABLE_LOCATIONS = "Locations";

    // column names
    private static final String COL_PRODUCT_ID = "ProductId";
    private static final String COL_PRODUCT_NAME = "ProductName";
    private static final String COL_MEAT_TYPE = "MeatType";

    private static final String COL_SUPPLIER_ID = "SupplierId";
    private static final String COL_DEST_ID = "DestId";
    private static final String COL_MASS = "Mass";
    private static final String COL_NUM_BOXES = "NumBoxes";
    private static final String COL_QUALITY = "Quality";

    private static final String COL_ORDER_ID = "OrderId";
    private static final String COL_ORDER_DATE = "OrderDate";
    private static final String COL_COMPLETED = "Completed";

    private static final String COL_QUANTITY = "Quantity";

    private static final String COL_CONTRACT_ID = "ContractId";
    private static final String COL_REPEAT_INTERVAL = "RepeatInterval";
    private static final String COL_REPEAT_ON = "RepeatOn";

    private static final String COL_LOCATION_ID = "LocationId";
    private static final String COL_LOCATION_NAME = "LocationName";
    private static final String COL_LOCATION_TYPE = "LocationType";
    private static final String COL_ADDR_1 = "AddrLine1";
    private static final String COL_ADDR_2 = "AddrLine2";
    private static final String COL_CITY = "City";
    private static final String COL_STATE = "State";
    private static final String COL_POSTCODE = "Postcode";
    private static final String COL_COUNTRY = "Country";
    private static final String COL_PHONE = "Phone";
    private static final String COL_EMAIL = "Email";
    //endregion database constants

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TODO onCreate
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO onUpgrade
    }
}
