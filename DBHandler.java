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

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
