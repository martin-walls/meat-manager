package com.martinwalls.nea.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressWarnings("FieldCanBeLocal")
public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stockDB.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    //region database constants
    // table names
    private final String TABLE_PRODUCTS = "Products";
    private final String TABLE_MEAT_TYPES = "MeatTypes";
    private final String TABLE_STOCK = "Stock";
    private final String TABLE_ORDERS = "Orders";
    private final String TABLE_ORDER_PRODUCTS = "OrderProducts";
    private final String TABLE_CONTRACTS = "Contracts";
    private final String TABLE_CONTRACT_PRODUCTS = "ContractProducts";
    private final String TABLE_LOCATIONS = "Locations";
    
    // cols for product table
    private final String PRODUCTS_ID = "ProductId";
    private final String PRODUCTS_NAME = "ProductName";
    private final String PRODUCTS_MEAT_TYPE = "MeatType";

    // cols for meat types table
    private final String MEAT_TYPES_TYPE = "MeatType";
    
    // cols for stock table
    private final String STOCK_PRODUCT_ID = "ProductId";
    private final String STOCK_LOCATION_ID = "LocationId";
    private final String STOCK_SUPPLIER_ID = "SupplierId";
    private final String STOCK_DEST_ID = "DestId";
    private final String STOCK_MASS = "Mass";
    private final String STOCK_NUM_BOXES = "NumBoxes";
    private final String STOCK_QUALITY = "Quality";

    // cols for orders table
    private final String ORDERS_ID = "OrderId";
    private final String ORDERS_DEST_ID = "DestId";
    private final String ORDERS_DATE = "OrderDate";
    private final String ORDERS_COMPLETED = "Completed";
    
    // cols for order products table
    private final String ORDER_PRODUCTS_PRODUCT_ID = "ProductId";
    private final String ORDER_PRODUCTS_ORDER_ID = "OrderId";
    private final String ORDER_PRODUCTS_QUANTITY_MASS = "QuantityMass";
    private final String ORDER_PRODUCTS_QUANTITY_BOXES = "QuantityBoxes";

    // cols for contracts table
    private final String CONTRACTS_ID = "ContractId";
    private final String CONTRACTS_DEST_ID = "DestId";
    private final String CONTRACTS_REPEAT_INTERVAL = "RepeatInterval";
    private final String CONTRACTS_REPEAT_ON = "RepeatOn";
    private final String CONTRACTS_REMINDER = "Reminder";
    
    // cols for contract products table
    private final String CONTRACT_PRODUCTS_CONTRACT_ID = "ContractId";
    private final String CONTRACT_PRODUCTS_PRODUCT_ID = "ProductId";
    private final String CONTRACT_PRODUCTS_QUANTITY_MASS = "QuantityMass";
    private final String CONTRACT_PRODUCTS_QUANTITY_BOXES = "QuantityBoxes";
    
    // cols for locations table
    private final String LOCATIONS_ID = "LocationId";
    private final String LOCATIONS_NAME = "LocationName";
    private final String LOCATIONS_TYPE = "LocationType";
    private final String LOCATIONS_ADDR_1 = "AddrLine1";
    private final String LOCATIONS_ADDR_2 = "AddrLine2";
    private final String LOCATIONS_CITY = "City";
    private final String LOCATIONS_POSTCODE = "Postcode";
    private final String LOCATIONS_COUNTRY = "Country";
    private final String LOCATIONS_PHONE = "Phone";
    private final String LOCATIONS_EMAIL = "Email";
    //endregion database constants

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //region create table queries
        String createMeatTypesTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_MEAT_TYPES + " ("
                + MEAT_TYPES_TYPE + " TEXT PRIMARY KEY )";
        db.execSQL(createMeatTypesTableQuery);

        String createProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_PRODUCTS + " ("
                + PRODUCTS_ID + " INTEGER PRIMARY KEY, "
                + PRODUCTS_NAME + " TEXT NOT NULL, "
                + PRODUCTS_MEAT_TYPE + " TEXT, "
                + "FOREIGN KEY (" + PRODUCTS_MEAT_TYPE + ") REFERENCES "
                + TABLE_MEAT_TYPES + "(" + MEAT_TYPES_TYPE + ") )";
        db.execSQL(createProductsTableQuery);

        String createLocationsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_LOCATIONS + " ("
                + LOCATIONS_ID + " INTEGER PRIMARY KEY, "
                + LOCATIONS_NAME + " TEXT NOT NULL, "
                + LOCATIONS_TYPE + " TEXT NOT NULL, "
                + LOCATIONS_ADDR_1 + " TEXT NOT NULL, "
                + LOCATIONS_ADDR_2 + " TEXT, "
                + LOCATIONS_CITY + " TEXT, "
                + LOCATIONS_POSTCODE + " TEXT NOT NULL, "
                + LOCATIONS_COUNTRY + " TEXT NOT NULL, "
                + LOCATIONS_PHONE + " TEXT, "
                + LOCATIONS_EMAIL + " TEXT )";
        db.execSQL(createLocationsTableQuery);

        String createStockTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_STOCK + " ("
                + STOCK_PRODUCT_ID + " INTEGER NOT NULL, "
                + STOCK_LOCATION_ID + " INTEGER NOT NULL, "
                + STOCK_SUPPLIER_ID + " INTEGER NOT NULL, "
                + STOCK_DEST_ID + " INTEGER, "
                + STOCK_MASS + " REAL NOT NULL, "
                + STOCK_NUM_BOXES + " REAL, "
                + STOCK_QUALITY + " INTEGER NOT NULL, "
                + "PRIMARY KEY (" + STOCK_PRODUCT_ID + ", "
                    + STOCK_LOCATION_ID + ", "
                    + STOCK_SUPPLIER_ID + "), "
                + "FOREIGN KEY ("+ STOCK_PRODUCT_ID + ") REFERENCES "
                    + TABLE_PRODUCTS + "(" + PRODUCTS_ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + STOCK_LOCATION_ID + ") REFERENCES "
                    + TABLE_LOCATIONS + "(" + LOCATIONS_ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + STOCK_SUPPLIER_ID + ") REFERENCES "
                    + TABLE_LOCATIONS + "(" + LOCATIONS_ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + STOCK_DEST_ID + ") REFERENCES "
                    + TABLE_LOCATIONS + "(" + LOCATIONS_ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createStockTableQuery);

        String createOrdersTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ORDERS + " ("
                + ORDERS_ID + " INTEGER PRIMARY KEY, "
                + ORDERS_DEST_ID + " INTEGER NOT NULL, "
                + ORDERS_DATE + " TEXT NOT NULL, "
                + ORDERS_COMPLETED + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + ORDERS_DEST_ID + ") REFERENCES "
                    + TABLE_LOCATIONS + "(" + LOCATIONS_ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createOrdersTableQuery);

        String createOrderProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_ORDER_PRODUCTS + " ("
                + ORDER_PRODUCTS_PRODUCT_ID + " INTEGER NOT NULL, "
                + ORDER_PRODUCTS_ORDER_ID + " INTEGER NOT NULL, "
                + ORDER_PRODUCTS_QUANTITY_MASS + " REAL NOT NULL, "
                + ORDER_PRODUCTS_QUANTITY_BOXES + " REAL, "
                + "PRIMARY KEY (" + ORDER_PRODUCTS_PRODUCT_ID + ", "
                    + ORDER_PRODUCTS_ORDER_ID + "), "
                + "FOREIGN KEY (" + ORDER_PRODUCTS_PRODUCT_ID + ") REFERENCES "
                    + TABLE_PRODUCTS + "(" + PRODUCTS_ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + ORDER_PRODUCTS_ORDER_ID + ") REFERENCES "
                    + TABLE_ORDERS + "(" + ORDERS_ID + ") "
                    + "ON DELETE CASCADE )";
        db.execSQL(createOrderProductsTableQuery);

        String createContractsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_CONTRACTS + " ("
                + CONTRACTS_ID + " INTEGER PRIMARY KEY, "
                + CONTRACTS_DEST_ID + " INTEGER NOT NULL, "
                + CONTRACTS_REPEAT_INTERVAL + " TEXT NOT NULL, "
                + CONTRACTS_REPEAT_ON + " TEXT NOT NULL, "
                + CONTRACTS_REMINDER + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + CONTRACTS_DEST_ID + ") REFERENCES "
                    + TABLE_LOCATIONS + "(" + LOCATIONS_ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createContractsTableQuery);

        String createContractProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + TABLE_CONTRACT_PRODUCTS + " ("
                + CONTRACT_PRODUCTS_CONTRACT_ID + " INTEGER NOT NULL, "
                + CONTRACT_PRODUCTS_PRODUCT_ID + " INTEGER NOT NULL, "
                + CONTRACT_PRODUCTS_QUANTITY_MASS + " REAL NOT NULL, "
                + CONTRACT_PRODUCTS_QUANTITY_BOXES + " REAL, "
                + "PRIMARY KEY (" + CONTRACT_PRODUCTS_CONTRACT_ID + ", "
                    + CONTRACT_PRODUCTS_PRODUCT_ID + "), "
                + "FOREIGN KEY (" + CONTRACT_PRODUCTS_CONTRACT_ID + ") REFERENCES "
                    + TABLE_CONTRACTS + "(" + CONTRACTS_ID + ") "
                    + "ON DELETE CASCADE, "
                + "FOREIGN KEY (" + CONTRACT_PRODUCTS_PRODUCT_ID + ") REFERENCES "
                    + TABLE_PRODUCTS + "(" + PRODUCTS_ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createContractProductsTableQuery);
        //endregion create table queries
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
