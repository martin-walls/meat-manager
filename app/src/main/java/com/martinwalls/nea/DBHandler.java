package com.martinwalls.nea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stockDB.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    //region database constants
    private enum productsTable {
        TABLE_NAME("Products"),
        COL_PRODUCT_ID("ProductId"),
        COL_PRODUCT_NAME("ProductName"),
        COL_MEAT_TYPE("MeatType");

        private String name;
        public productsTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum meatTypesTable {
        TABLE_NAME("MeatTypes"),
        COL_MEAT_TYPE("MeatType");

        private String name;
        public meatTypesTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    //todo maybe have data type eg INTEGER also stored in enums?
    private enum stockTable {
        TABLE_NAME("Stock"),
        COL_PRODUCT_ID("ProductId"),
        COL_LOCATION_ID("LocationId"),
        COL_SUPPLIER_ID("SupplierId"),
        COL_DEST_ID("DestId"),
        COL_MASS("Mass"),
        COL_NUM_BOXES("NumBoxes"),
        COL_QUALITY("Quality");

        private String name;
        public stockTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum ordersTable {
        TABLE_NAME("Orders"),
        COL_ORDER_ID("OrderId"),
        COL_DEST_ID("DestId"),
        COL_ORDER_DATE("OrderDate"),
        COL_COMPLETED("Completed");

        private String name;
        public ordersTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum orderProductsTable {
        TABLE_NAME("OrderProducts"),
        COL_PRODUCT_ID("ProductId"),
        COL_ORDER_ID("OrderId"),
        COL_QUANTITY_MASS("QuantityMass"),
        COL_QUANTITY_BOXES("QuantityBoxes");

        private String name;
        public orderProductsTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum contractsTable {
        TABLE_NAME("Contracts"),
        COL_CONTRACT_ID("ContractId"),
        COL_DEST_ID("DestId"),
        COL_REPEAT_INTERVAL("RepeatInterval"),
        COL_REPEAT_ON("RepeatOn"),
        COL_REMINDER("Reminder");

        private String name;
        public contractsTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum contractProductsTable {
        TABLE_NAME("ContractProducts"),
        COL_CONTRACT_ID("ContractId"),
        COL_PRODUCT_ID("ProductId"),
        COL_QUANTITY_MASS("QuantityMass"),
        COL_QUANTITY_BOXES("QuantityBoxes");

        private String name;
        public contractProductsTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }

    private enum locationsTable {
        TABLE_NAME("Locations"),
        COL_LOCATION_ID("LocationId"),
        COL_LOCATION_NAME("LocationName"),
        COL_LOCATION_TYPE("LocationType"),
        COL_ADDR_1("AddrLine1"),
        COL_ADDR_2("AddrLine2"),
        COL_CITY("City"),
        COL_POSTCODE("Postcode"),
        COL_COUNTRY("Country"),
        COL_PHONE("Phone"),
        COL_EMAIL("Email");

        private String name;
        public locationsTable(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    //endregion database constants

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMeatTypesTableQuery = "CREATE TABLE IF NOT EXISTS "
                + meatTypesTable.TABLE_NAME.getName() + " ("
                + meatTypesTable.COL_MEAT_TYPE.getName() + " TEXT PRIMARY KEY )";
        db.execSQL(createMeatTypesTableQuery);

        String createProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + productsTable.TABLE_NAME.getName() + " ("
                + productsTable.COL_PRODUCT_ID.getName() + " INTEGER PRIMARY KEY, "
                + productsTable.COL_PRODUCT_NAME.getName() + " TEXT NOT NULL, "
                + productsTable.COL_MEAT_TYPE.getName() + " TEXT, "
                + "FOREIGN KEY (" + productsTable.COL_MEAT_TYPE.getName() + ") REFERENCES "
                + meatTypesTable.TABLE_NAME.getName() + "(" + meatTypesTable.COL_MEAT_TYPE.getName() + ")";
        db.execSQL(createProductsTableQuery);

        String createLocationsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + locationsTable.TABLE_NAME.getName() + " ("
                + locationsTable.COL_LOCATION_ID.getName() + " INTEGER PRIMARY KEY, "
                + locationsTable.COL_LOCATION_NAME.getName() + " TEXT NOT NULL, "
                + locationsTable.COL_LOCATION_TYPE.getName() + " TEXT NOT NULL, "
                + locationsTable.COL_ADDR_1.getName() + " TEXT NOT NULL, "
                + locationsTable.COL_ADDR_2.getName() + " TEXT, "
                + locationsTable.COL_CITY.getName() + " TEXT, "
                + locationsTable.COL_POSTCODE.getName() + " TEXT NOT NULL, "
                + locationsTable.COL_COUNTRY.getName() + " TEXT NOT NULL, "
                + locationsTable.COL_PHONE.getName() + " TEXT, "
                + locationsTable.COL_EMAIL.getName() + " TEXT )";
        db.execSQL(createLocationsTableQuery);

        String createStockTableQuery = "CREATE TABLE IF NOT EXISTS "
                + stockTable.TABLE_NAME.getName() + " ("
                + stockTable.COL_PRODUCT_ID.getName() + " INTEGER NOT NULL, "
                + stockTable.COL_LOCATION_ID.getName() + " INTEGER NOT NULL, "
                + stockTable.COL_SUPPLIER_ID.getName() + " INTEGER NOT NULL, "
                + stockTable.COL_DEST_ID.getName() + " INTEGER, "
                + stockTable.COL_MASS.getName() + " REAL NOT NULL, "
                + stockTable.COL_NUM_BOXES.getName() + " REAL, "
                + stockTable.COL_QUALITY.getName() + " INTEGER NOT NULL, "
                + "PRIMARY KEY (" + stockTable.COL_PRODUCT_ID.getName() + ", "
                    + stockTable.COL_LOCATION_ID.getName() + ", "
                    + stockTable.COL_SUPPLIER_ID.getName() + "), "
                + "FOREIGN KEY ("+ stockTable.COL_PRODUCT_ID.getName() + ") REFERENCES "
                    + productsTable.TABLE_NAME.getName() + "(" + productsTable.COL_PRODUCT_ID.getName() + ") "
                    + "ON DELETE RESTRICT,",
                + "FOREIGN KEY (" + stockTable.COL_LOCATION_ID.getName() + ") REFERENCES "
                    + locationsTable.TABLE_NAME.getName() + "(" + locationsTable.COL_LOCATION_ID.getName() + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + stockTable.COL_SUPPLIER_ID.getName() + ") REFERENCES "
                    + locationsTable.TABLE_NAME.getName() + "(" + locationsTable.COL_LOCATION_ID.getName() + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + stockTable.COL_DEST_ID.getName() + ") REFERENCES "
                    + locationsTable.TABLE_NAME.getName() + "(" + locationsTable.COL_LOCATION_ID.getName() + ") "
                    + "ON DELETE RESTRICT )"
        db.execSQL(createStockTableQuery);

        String createOrdersTableQuery = "CREATE TABLE IF NOT EXISTS "
                + ordersTable.TABLE_NAME.getName() + " ("
                + ordersTable.COL_ORDER_ID.getName() + " INTEGER PRIMARY KEY, "
                + ordersTable.COL_DEST_ID.getName() + " INTEGER NOT NULL, "
                + ordersTable.COL_ORDER_DATE.getName() + " TEXT NOT NULL, "
                + ordersTable.COL_COMPLETED.getName() + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + ordersTable.COL_DEST_ID.getName() + ") REFERENCES "
                    + locationsTable.TABLE_NAME.getName() + "(" + locationsTable.COL_LOCATION_ID.getName() + ") "
                    + "ON DELETE RESTRICT )"
        db.execSQL(createOrdersTableQuery);

        String createOrderProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + orderProductsTable.TABLE_NAME.getName() + " ("
                + orderProductsTable.COL_PRODUCT_ID.getName() + " INTEGER NOT NULL, "
                + orderProductsTable.COL_ORDER_ID.getName() + " INTEGER NOT NULL, "
                + orderProductsTable.COL_QUANTITY_MASS.getName() + " REAL NOT NULL, "
                + orderProductsTable.COL_QUANTITY_BOXES.getName() + " REAL, "
                + "PRIMARY KEY (" + orderProductsTable.COL_PRODUCT_ID.getName() + ", "
                    + orderProductsTable.COL_ORDER_ID + "), "
                + "FOREIGN KEY (" + orderProductsTable.COL_PRODUCT_ID.getName() + ") REFERENCES "
                    + productsTable.TABLE_NAME.getName() + "(" + productsTable.COL_PRODUCT_ID.getName() + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + orderProductsTable.COL_ORDER_ID.getName() + ") REFERENCES "
                    + ordersTable.TABLE_NAME.getName() + "(" + ordersTable.COL_ORDER_ID.getName() + ") "
                    + "ON DELETE CASCADE )"
        db.execSQL(createOrderProductsTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
