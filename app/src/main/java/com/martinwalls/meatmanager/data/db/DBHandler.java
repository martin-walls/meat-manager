package com.martinwalls.meatmanager.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import com.martinwalls.meatmanager.data.models.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stockDB.db";
    private static final int DATABASE_VERSION = 7;

    //region db constants
    private final class ProductsTable {
        static final String TABLE_NAME = "Products";
        static final String ID = "ProductId";
        static final String NAME = "ProductName";
        static final String MEAT_TYPE = "MeatType";
    }

    private final class MeatTypesTable {
        static final String TABLE_NAME = "MeatTypes";
        static final String MEAT_TYPE = "MeatType";
    }
    
    private final class StockTable {
        static final String TABLE_NAME = "Stock";
        static final String ID = "StockId";
        static final String PRODUCT_ID = "ProductId";
        static final String LOCATION_ID = "LocationId";
        static final String SUPPLIER_ID = "SupplierId";
        static final String DEST_ID = "DestId"; //todo remove dest
        static final String MASS = "Mass";
        static final String NUM_BOXES = "NumBoxes";
        static final String QUALITY = "Quality";
        //todo add intake date & best before date
    }

    private final class OrdersTable {
        static final String TABLE_NAME = "Orders";
        static final String ID = "OrderId";
        static final String DEST_ID = "DestId";
        static final String ORDER_DATE = "OrderDate";
        static final String COMPLETED = "Completed";
    }

    private final class OrderProductsTable {
        static final String TABLE_NAME = "OrderProducts";
        static final String PRODUCT_ID = "ProductId";
        static final String ORDER_ID = "OrderId";
        static final String QUANTITY_MASS = "QuantityMass";
        static final String QUANTITY_BOXES = "QuantityBoxes";
    }

    private final class ContractsTable {
        static final String TABLE_NAME = "Contracts";
        static final String ID = "ContractId";
        static final String DEST_ID = "DestId";
        static final String REPEAT_INTERVAL = "RepeatInterval";
        static final String REPEAT_INTERVAL_UNIT = "RepeatIntervalUnit";
        static final String REPEAT_ON = "RepeatOn";
        static final String START_DATE = "StartDate";
        static final String REMINDER = "Reminder";
    }

    private final class ContractProductsTable {
        static final String TABLE_NAME = "ContractProducts";
        static final String CONTRACT_ID = "ContractId";
        static final String PRODUCT_ID = "ProductId";
        static final String QUANTITY_MASS = "QuantityMass";
        static final String QUANTITY_BOXES = "QuantityBoxes";
    }

    private final class LocationsTable {
        static final String TABLE_NAME = "Locations";
        static final String ID = "LocationId";
        static final String NAME = "LocationName";
        static final String TYPE = "LocationType";
        static final String ADDR_1 = "AddrLine1";
        static final String ADDR_2 = "AddrLine2";
        static final String CITY = "City";
        static final String POSTCODE = "Postcode";
        static final String COUNTRY = "Country";
        static final String PHONE = "Phone";
        static final String EMAIL = "Email";
    }
    //endregion db constants

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Initialises the database. Creates all tables in the database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //region create table queries
        String createMeatTypesTableQuery = "CREATE TABLE IF NOT EXISTS "
                + MeatTypesTable.TABLE_NAME + " ("
                + MeatTypesTable.MEAT_TYPE + " TEXT PRIMARY KEY )";
        db.execSQL(createMeatTypesTableQuery);

        String createProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + ProductsTable.TABLE_NAME + " ("
                + ProductsTable.ID + " INTEGER PRIMARY KEY, "
                + ProductsTable.NAME + " TEXT NOT NULL UNIQUE, "
                + ProductsTable.MEAT_TYPE + " TEXT, "
                + "FOREIGN KEY (" + ProductsTable.MEAT_TYPE + ") REFERENCES "
                + MeatTypesTable.TABLE_NAME + "(" + MeatTypesTable.MEAT_TYPE + ")"
                + " ON DELETE RESTRICT )";
        db.execSQL(createProductsTableQuery);

        String createLocationsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + LocationsTable.TABLE_NAME + " ("
                + LocationsTable.ID + " INTEGER PRIMARY KEY, "
                + LocationsTable.NAME + " TEXT NOT NULL, "
                + LocationsTable.TYPE + " TEXT NOT NULL, "
                + LocationsTable.ADDR_1 + " TEXT NOT NULL, "
                + LocationsTable.ADDR_2 + " TEXT, "
                + LocationsTable.CITY + " TEXT, "
                + LocationsTable.POSTCODE + " TEXT NOT NULL, "
                + LocationsTable.COUNTRY + " TEXT NOT NULL, "
                + LocationsTable.PHONE + " TEXT, "
                + LocationsTable.EMAIL + " TEXT )";
        db.execSQL(createLocationsTableQuery);

        String createStockTableQuery = "CREATE TABLE IF NOT EXISTS "
                + StockTable.TABLE_NAME + " ("
                + StockTable.ID + " INTEGER PRIMARY KEY,"
                + StockTable.PRODUCT_ID + " INTEGER NOT NULL, "
                + StockTable.LOCATION_ID + " INTEGER NOT NULL, "
                + StockTable.SUPPLIER_ID + " INTEGER NOT NULL, "
                + StockTable.DEST_ID + " INTEGER, "
                + StockTable.MASS + " REAL NOT NULL, "
                + StockTable.NUM_BOXES + " INTEGER, "
                + StockTable.QUALITY + " TEXT NOT NULL, "
                + "FOREIGN KEY ("+ StockTable.PRODUCT_ID + ") REFERENCES "
                    + ProductsTable.TABLE_NAME + "(" + ProductsTable.ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + StockTable.LOCATION_ID + ") REFERENCES "
                    + LocationsTable.TABLE_NAME + "(" + LocationsTable.ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + StockTable.SUPPLIER_ID + ") REFERENCES "
                    + LocationsTable.TABLE_NAME + "(" + LocationsTable.ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + StockTable.DEST_ID + ") REFERENCES "
                    + LocationsTable.TABLE_NAME + "(" + LocationsTable.ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createStockTableQuery);

        String createOrdersTableQuery = "CREATE TABLE IF NOT EXISTS "
                + OrdersTable.TABLE_NAME + " ("
                + OrdersTable.ID + " INTEGER PRIMARY KEY, "
                + OrdersTable.DEST_ID + " INTEGER NOT NULL, "
                + OrdersTable.ORDER_DATE + " INTEGER NOT NULL, "
                + OrdersTable.COMPLETED + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + OrdersTable.DEST_ID + ") REFERENCES "
                    + LocationsTable.TABLE_NAME + "(" + LocationsTable.ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createOrdersTableQuery);

        String createOrderProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + OrderProductsTable.TABLE_NAME + " ("
                + OrderProductsTable.PRODUCT_ID + " INTEGER NOT NULL, "
                + OrderProductsTable.ORDER_ID + " INTEGER NOT NULL, "
                + OrderProductsTable.QUANTITY_MASS + " REAL NOT NULL, "
                + OrderProductsTable.QUANTITY_BOXES + " INTEGER, "
                + "PRIMARY KEY (" + OrderProductsTable.PRODUCT_ID + ", "
                    + OrderProductsTable.ORDER_ID + "), "
                + "FOREIGN KEY (" + OrderProductsTable.PRODUCT_ID + ") REFERENCES "
                    + ProductsTable.TABLE_NAME + "(" + ProductsTable.ID + ") "
                    + "ON DELETE RESTRICT,"
                + "FOREIGN KEY (" + OrderProductsTable.ORDER_ID + ") REFERENCES "
                    + OrdersTable.TABLE_NAME + "(" + OrdersTable.ID + ") "
                    + "ON DELETE CASCADE )";
        db.execSQL(createOrderProductsTableQuery);

        String createContractsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + ContractsTable.TABLE_NAME + " ("
                + ContractsTable.ID + " INTEGER PRIMARY KEY, "
                + ContractsTable.DEST_ID + " INTEGER NOT NULL, "
                + ContractsTable.REPEAT_INTERVAL + " INTEGER NOT NULL, "
                + ContractsTable.REPEAT_INTERVAL_UNIT + " TEXT NOT NULL, "
                + ContractsTable.REPEAT_ON + " INTEGER, "
                + ContractsTable.START_DATE + " INTEGER NOT NULL, "
                + ContractsTable.REMINDER + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + ContractsTable.DEST_ID + ") REFERENCES "
                    + LocationsTable.TABLE_NAME + "(" + LocationsTable.ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createContractsTableQuery);

        String createContractProductsTableQuery = "CREATE TABLE IF NOT EXISTS "
                + ContractProductsTable.TABLE_NAME + " ("
                + ContractProductsTable.CONTRACT_ID + " INTEGER NOT NULL, "
                + ContractProductsTable.PRODUCT_ID + " INTEGER NOT NULL, "
                + ContractProductsTable.QUANTITY_MASS + " REAL NOT NULL, "
                + ContractProductsTable.QUANTITY_BOXES + " INTEGER, "
                + "PRIMARY KEY (" + ContractProductsTable.CONTRACT_ID + ", "
                    + ContractProductsTable.PRODUCT_ID + "), "
                + "FOREIGN KEY (" + ContractProductsTable.CONTRACT_ID + ") REFERENCES "
                    + ContractsTable.TABLE_NAME + "(" + ContractsTable.ID + ") "
                    + "ON DELETE CASCADE, "
                + "FOREIGN KEY (" + ContractProductsTable.PRODUCT_ID + ") REFERENCES "
                    + ProductsTable.TABLE_NAME + "(" + ProductsTable.ID + ") "
                    + "ON DELETE RESTRICT )";
        db.execSQL(createContractProductsTableQuery);
        //endregion create table queries
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        // make sure foreign keys are enabled
        db.setForeignKeyConstraintsEnabled(true);
    }

    //region product

    /**
     * Queries the database for a {@link Product} with the specified ID.
     */
    public Product getProduct(int productId) {
        Product productResult = new Product();
        String query = "SELECT * FROM " + ProductsTable.TABLE_NAME
                + " WHERE " + ProductsTable.ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{productId + ""});
        if (cursor.moveToFirst()) {
            productResult.setProductId(productId);
            productResult.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            productResult.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
        }
        cursor.close();
        db.close();
        return productResult;
    }

    /**
     * Gets all products in the database.
     */
    public List<Product> getAllProducts() {
        List<Product> productResultList = new ArrayList<>();
        String query = "SELECT * FROM " + ProductsTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.ID)));
            product.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            product.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            productResultList.add(product);
        }
        cursor.close();
        db.close();
        return productResultList;
    }

    /**
     * Queries the database for all products that are used in orders or
     * contracts. Returns a list of {@link ProductQuantity} objects that
     * also store how much of each product is required.
     */
    public List<ProductQuantity> getAllProductsRequired() {
        List<ProductQuantity> productQuantityResultList = new ArrayList<>();
        // not completed orders
        String orderProductsQuery = "SELECT "
                + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.PRODUCT_ID + ","
                + OrderProductsTable.QUANTITY_MASS + ","
                + OrderProductsTable.QUANTITY_BOXES + ","
                + ProductsTable.NAME + ","
                + ProductsTable.MEAT_TYPE + ","
                + OrdersTable.COMPLETED
                + " FROM " + OrderProductsTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + OrdersTable.TABLE_NAME + " ON "
                + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.ORDER_ID
                + "=" + OrdersTable.TABLE_NAME + "." + OrdersTable.ID
                + " WHERE " + OrdersTable.COMPLETED + "=0";
        String contractProductsQuery = "SELECT "
                + ContractProductsTable.TABLE_NAME + "."
                    + ContractProductsTable.PRODUCT_ID + ","
                + ContractProductsTable.QUANTITY_MASS + ","
                + ContractProductsTable.QUANTITY_BOXES + ","
                + ProductsTable.NAME + ","
                + ProductsTable.MEAT_TYPE
                + " FROM " + ContractProductsTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + ContractProductsTable.TABLE_NAME + "." + ContractProductsTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(orderProductsQuery, null);
        while (cursor.moveToNext()) {
            ProductQuantity productQuantity = new ProductQuantity();
            Product product = new Product();
            product.setProductId(cursor.getInt(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.PRODUCT_ID)));
            product.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            product.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            productQuantity.setProduct(product);
            productQuantity.setQuantityMass(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.QUANTITY_MASS)));
            productQuantity.setQuantityBoxes(cursor.getInt(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.QUANTITY_BOXES)));
            productQuantityResultList.add(productQuantity);
        }
        cursor = db.rawQuery(contractProductsQuery, null);
        while (cursor.moveToNext()) {
            ProductQuantity productQuantity = new ProductQuantity();
            Product product = new Product();
            product.setProductId(cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.PRODUCT_ID)));
            product.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            product.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            productQuantity.setProduct(product);
            productQuantity.setQuantityMass(cursor.getDouble(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.QUANTITY_MASS)));
            productQuantity.setQuantityBoxes(cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.QUANTITY_BOXES)));
            productQuantityResultList.add(productQuantity);
        }
        cursor.close();
        db.close();
        return productQuantityResultList;
    }

    /**
     * Stores a {@link Product} in the database.
     */
    @Deprecated
    public boolean addProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put(ProductsTable.NAME, product.getProductName());
        values.put(ProductsTable.MEAT_TYPE, product.getMeatType());

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(ProductsTable.TABLE_NAME, null, values);
        db.close();
        return newRowId != -1;
    }

    /**
     * Stores a {@link Product} in the database. Returns the new ID.
     */
    public int addProduct(@NonNull Product product, boolean getId) {
        ContentValues values = new ContentValues();
        values.put(ProductsTable.NAME, product.getProductName());
        values.put(ProductsTable.MEAT_TYPE, product.getMeatType());

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(ProductsTable.TABLE_NAME, null, values);
        db.close();
        return (int) newRowId;
    }

    /**
     * Queries the database for any usages of the {@link Product} with this ID
     * in stock, contracts or orders. It is safe to delete if no usages are found.
     */
    public boolean isProductSafeToDelete(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String stockQuery = "SELECT * FROM " + StockTable.TABLE_NAME
                + " WHERE " + StockTable.PRODUCT_ID + "=?";
        String contractsQuery = "SELECT * FROM " + ContractProductsTable.TABLE_NAME
                + " WHERE " + ContractProductsTable.PRODUCT_ID + "=?";
        String ordersQuery = "SELECT * FROM " + OrderProductsTable.TABLE_NAME
                + " WHERE " + OrderProductsTable.PRODUCT_ID + "=?";
        String[] selectionArgs = new String[] {productId + ""};
        boolean safeToDelete = true;
        for (String query : new String[]{stockQuery, contractsQuery, ordersQuery}) {
            if (!safeToDelete) {
                break;
            }
            Cursor cursor = db.rawQuery(query, selectionArgs);
            int numRows = cursor.getCount();
            safeToDelete = numRows == 0;
            cursor.close();
        }
        db.close();
        return safeToDelete;
    }

    /**
     * Removes the {@link Product} with this ID from the database if it exists.
     */
    public boolean deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(ProductsTable.TABLE_NAME,
                ProductsTable.ID + "=?", new String[]{productId + ""});
        db.close();
        return deletedRows == 1;
    }
    //endregion product

    //region meat types

    /**
     * Gets all meat types in the database.
     */
    public List<String> getAllMeatTypes() {
        List<String> meatTypeResultList = new ArrayList<>();
        String query = "SELECT * FROM " + MeatTypesTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            meatTypeResultList.add(
                    cursor.getString(cursor.getColumnIndexOrThrow(MeatTypesTable.MEAT_TYPE)));
        }
        cursor.close();
        db.close();
        return meatTypeResultList;
    }

    /**
     * Stores a meat type in the database.
     */
    public boolean addMeatType(String meatType) {
        ContentValues values = new ContentValues();
        values.put(MeatTypesTable.MEAT_TYPE, meatType);
        SQLiteDatabase db = this.getWritableDatabase();
        // id of inserted row, -1 if error
        long newRowId = db.insert(MeatTypesTable.TABLE_NAME, null, values);
        db.close();
        return newRowId != -1;
    }

    /**
     * Removes this meat type from the database.
     */
    public boolean deleteMeatType(String meatType) {
        SQLiteDatabase db = this.getWritableDatabase();
        boolean success;
        try {
            success = db.delete(MeatTypesTable.TABLE_NAME,
                    MeatTypesTable.MEAT_TYPE + "=?", new String[]{meatType}) == 1;
        } catch (SQLiteConstraintException e) {
            success = false;
        }
        db.close();
        return success;
    }

    /**
     * Queries the database to see if the given meat type has already been added.
     */
    public boolean isMeatTypeInDb(String meatType) {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, MeatTypesTable.TABLE_NAME,
                MeatTypesTable.MEAT_TYPE + "=?", new String[]{meatType});
        return numRows > 0;
    }
    //endregion meat types

    //region stock

    /**
     * Queries the database for a {@link StockItem} with the specified ID.
     */
    public StockItem getStockItem(int stockId) {
        StockItem stockResult = new StockItem();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT "
                + StockTable.ID + ","
                + StockTable.TABLE_NAME + "."
                + StockTable.PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.NAME + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.NAME
                    + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LocationsTable.ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LocationsTable.NAME + " AS " + ALIAS_DEST_NAME + ","
                + StockTable.MASS + ","
                + StockTable.NUM_BOXES + ","
                + StockTable.QUALITY + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.NAME + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.MEAT_TYPE
                + " FROM " + StockTable.TABLE_NAME
                + " LEFT JOIN " + ProductsTable.TABLE_NAME + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_LOCATION + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + "=" + ALIAS_LOCATION + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_SUPPLIER + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.SUPPLIER_ID
                + "=" + ALIAS_SUPPLIER + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_DEST + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.DEST_ID
                + "=" + ALIAS_DEST + "." + LocationsTable.ID
                + " WHERE " + StockTable.ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{stockId + ""});
        if (cursor.moveToFirst()) {
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.PRODUCT_ID)));
            stockProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            stockProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            stockResult.setProduct(stockProduct);
            stockResult.setStockId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.ID)));
            stockResult.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            stockResult.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            stockResult.setSupplierId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockResult.setSupplierName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID))) {
                stockResult.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            } else {
                stockResult.setDestId(-1);
            }
            stockResult.setDestName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockResult.setMass(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StockTable.MASS)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES))) {
                stockResult.setNumBoxes(
                        cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES)));
            } else {
                stockResult.setNumBoxes(-1);
            }
            stockResult.setQuality(StockItem.Quality.parseQuality(
                    cursor.getString(cursor.getColumnIndexOrThrow(StockTable.QUALITY))));
        }
        cursor.close();
        db.close();
        return stockResult;
    }

    /**
     * Gets all stock items in the database.
     */
    public List<StockItem> getAllStock() {
        List<StockItem> stockResultList = new ArrayList<>();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT "
                + StockTable.ID + ","
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.NAME + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.NAME
                    + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LocationsTable.ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LocationsTable.NAME + " AS " + ALIAS_DEST_NAME + ","
                + StockTable.MASS + ","
                + StockTable.NUM_BOXES + ","
                + StockTable.QUALITY + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.NAME + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.MEAT_TYPE
                + " FROM " + StockTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_LOCATION + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + "=" + ALIAS_LOCATION + "." + LocationsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_SUPPLIER + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.SUPPLIER_ID
                + "=" + ALIAS_SUPPLIER + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_DEST + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.DEST_ID
                + "=" + ALIAS_DEST + "." + LocationsTable.ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            StockItem stockItem = new StockItem();
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.PRODUCT_ID)));
            stockProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            stockProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            stockItem.setProduct(stockProduct);
            stockItem.setStockId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.ID)));
            stockItem.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            stockItem.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            stockItem.setSupplierId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockItem.setSupplierName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID))) {
                stockItem.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            } else {
                stockItem.setDestId(-1);
            }
            stockItem.setDestName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockItem.setMass(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StockTable.MASS)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES))) {
                stockItem.setNumBoxes(
                        cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES)));
            } else {
                stockItem.setNumBoxes(-1);
            }
            stockItem.setQuality(StockItem.Quality.parseQuality(cursor.getString(
                    cursor.getColumnIndexOrThrow(StockTable.QUALITY))));
            stockResultList.add(stockItem);
        }
        cursor.close();
        db.close();
        return stockResultList;
    }

    /**
     * Gets all stock items in the database, grouped by product.
     */
    public List<StockItem> getAllStockByProduct() {
        List<StockItem> stockResultList = new ArrayList<>();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT "
                + StockTable.ID + ","
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.NAME + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.NAME
                + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LocationsTable.ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LocationsTable.NAME + " AS " + ALIAS_DEST_NAME + ","
                + StockTable.MASS + ","
                + StockTable.NUM_BOXES + ","
                + StockTable.QUALITY + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.NAME + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.MEAT_TYPE
                + " FROM " + StockTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_LOCATION + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + "=" + ALIAS_LOCATION + "." + LocationsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_SUPPLIER + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.SUPPLIER_ID
                + "=" + ALIAS_SUPPLIER + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_DEST + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.DEST_ID
                + "=" + ALIAS_DEST + "." + LocationsTable.ID
                + " GROUP BY " + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            StockItem stockItem = new StockItem();
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.PRODUCT_ID)));
            stockProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            stockProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            stockItem.setProduct(stockProduct);
            stockItem.setStockId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.ID)));
            stockItem.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            stockItem.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            stockItem.setSupplierId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockItem.setSupplierName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID))) {
                stockItem.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            } else {
                stockItem.setDestId(-1);
            }
            stockItem.setDestName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockItem.setMass(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StockTable.MASS)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES))) {
                stockItem.setNumBoxes(
                        cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES)));
            } else {
                stockItem.setNumBoxes(-1);
            }
            stockItem.setQuality(StockItem.Quality.parseQuality(cursor.getString(
                    cursor.getColumnIndexOrThrow(StockTable.QUALITY))));
            stockResultList.add(stockItem);
        }
        cursor.close();
        db.close();
        return stockResultList;
    }

    /**
     * Queries the database for all stock items in the {@link Location} with
     * the given ID. Groups results by product.
     */
    public List<StockItem> getAllStockFromLocationByProduct(int locationId) {
        List<StockItem> stockResultList = new ArrayList<>();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT "
                + StockTable.ID + ","
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.NAME + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.NAME
                    + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LocationsTable.ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LocationsTable.NAME + " AS " + ALIAS_DEST_NAME + ","
                + StockTable.MASS + ","
                + StockTable.NUM_BOXES + ","
                + StockTable.QUALITY + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.NAME + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.MEAT_TYPE
                + " FROM " + StockTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_LOCATION + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + "=" + ALIAS_LOCATION + "." + LocationsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_SUPPLIER + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.SUPPLIER_ID
                + "=" + ALIAS_SUPPLIER + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_DEST + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.DEST_ID
                + "=" + ALIAS_DEST + "." + LocationsTable.ID
                + " WHERE " + ALIAS_LOCATION + "." + LocationsTable.ID + "=?"
                + " GROUP BY " + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{locationId + ""});
        while (cursor.moveToNext()) {
            StockItem stockItem = new StockItem();
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.PRODUCT_ID)));
            stockProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            stockProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            stockItem.setProduct(stockProduct);
            stockItem.setStockId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.ID)));
            stockItem.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            stockItem.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            stockItem.setSupplierId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockItem.setSupplierName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID))) {
                stockItem.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            } else {
                stockItem.setDestId(-1);
            }
            stockItem.setDestName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockItem.setMass(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StockTable.MASS)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES))) {
                stockItem.setNumBoxes(
                        cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES)));
            } else {
                stockItem.setNumBoxes(-1);
            }
            stockItem.setQuality(StockItem.Quality.parseQuality(cursor.getString(
                    cursor.getColumnIndexOrThrow(StockTable.QUALITY))));
            stockResultList.add(stockItem);
        }
        cursor.close();
        db.close();
        return stockResultList;
    }

    /**
     * Queries the database for all stock items for products connected to the
     * {@link Order} with the given ID.
     */
    public List<StockItem> getAllStockForOrder(int orderId) {
        List<StockItem> stockResultList = new ArrayList<>();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT "
                + StockTable.ID + ","
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.NAME + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.NAME
                    + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LocationsTable.ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LocationsTable.NAME + " AS " + ALIAS_DEST_NAME + ","
                + StockTable.MASS + ","
                + StockTable.NUM_BOXES + ","
                + StockTable.QUALITY + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.NAME + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.MEAT_TYPE
                + " FROM " + StockTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + OrderProductsTable.TABLE_NAME + " ON "
                + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + "=" + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.PRODUCT_ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_LOCATION + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + "=" + ALIAS_LOCATION + "." + LocationsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_SUPPLIER + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.SUPPLIER_ID
                + "=" + ALIAS_SUPPLIER + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_DEST + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.DEST_ID
                + "=" + ALIAS_DEST + "." + LocationsTable.ID
                + " WHERE " + OrderProductsTable.ORDER_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{orderId + ""});
        while (cursor.moveToNext()) {
            StockItem stockItem = new StockItem();
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.PRODUCT_ID)));
            stockProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            stockProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            stockItem.setProduct(stockProduct);
            stockItem.setStockId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.ID)));
            stockItem.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            stockItem.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            stockItem.setSupplierId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockItem.setSupplierName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID))) {
                stockItem.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            } else {
                stockItem.setDestId(-1);
            }
            stockItem.setDestName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockItem.setMass(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StockTable.MASS)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES))) {
                stockItem.setNumBoxes(
                        cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES)));
            } else {
                stockItem.setNumBoxes(-1);
            }
            stockItem.setQuality(StockItem.Quality.parseQuality(cursor.getString(
                    cursor.getColumnIndexOrThrow(StockTable.QUALITY))));
            stockResultList.add(stockItem);
        }
        cursor.close();
        db.close();
        return stockResultList;
    }

    public List<StockItem> getAllStockForContract(int contractId) {
        List<StockItem> stockResultList = new ArrayList<>();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT "
                + StockTable.ID + ","
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.ID + ","
                + ALIAS_LOCATION + "." + LocationsTable.NAME + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LocationsTable.NAME
                + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LocationsTable.ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LocationsTable.NAME + " AS " + ALIAS_DEST_NAME + ","
                + StockTable.MASS + ","
                + StockTable.NUM_BOXES + ","
                + StockTable.QUALITY + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.NAME + ","
                + ProductsTable.TABLE_NAME + "." + ProductsTable.MEAT_TYPE
                + " FROM " + StockTable.TABLE_NAME
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + ContractProductsTable.TABLE_NAME + " ON "
                + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + "=" + ContractProductsTable.TABLE_NAME
                    + "." + ContractProductsTable.PRODUCT_ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_LOCATION + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + "=" + ALIAS_LOCATION + "." + LocationsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_SUPPLIER + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.SUPPLIER_ID
                + "=" + ALIAS_SUPPLIER + "." + LocationsTable.ID
                + " LEFT JOIN " + LocationsTable.TABLE_NAME + " AS " + ALIAS_DEST + " ON "
                + StockTable.TABLE_NAME + "." + StockTable.DEST_ID
                + "=" + ALIAS_DEST + "." + LocationsTable.ID
                + " WHERE " + ContractProductsTable.CONTRACT_ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{contractId + ""});
        while (cursor.moveToNext()) {
            //todo refactor these parts into method eg. getStockInfoFromCursor() or similar
            StockItem stockItem = new StockItem();
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.PRODUCT_ID)));
            stockProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            stockProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            stockItem.setProduct(stockProduct);
            stockItem.setStockId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.ID)));
            stockItem.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            stockItem.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            stockItem.setSupplierId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockItem.setSupplierName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID))) {
                stockItem.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            } else {
                stockItem.setDestId(-1);
            }
            stockItem.setDestName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockItem.setMass(
                    cursor.getDouble(cursor.getColumnIndexOrThrow(StockTable.MASS)));
            if (!cursor.isNull(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES))) {
                stockItem.setNumBoxes(
                        cursor.getInt(cursor.getColumnIndexOrThrow(StockTable.NUM_BOXES)));
            } else {
                stockItem.setNumBoxes(-1);
            }
            stockItem.setQuality(StockItem.Quality.parseQuality(cursor.getString(
                    cursor.getColumnIndexOrThrow(StockTable.QUALITY))));
            stockResultList.add(stockItem);
        }
        cursor.close();
        db.close();
        return stockResultList;
    }

    /**
     * Stores a {@link StockItem} in the database.
     */
    public int addStockItem(StockItem stockItem) {
        ContentValues values = new ContentValues();
        values.put(StockTable.PRODUCT_ID, stockItem.getProduct().getProductId());
        values.put(StockTable.LOCATION_ID, stockItem.getLocationId());
        values.put(StockTable.SUPPLIER_ID, stockItem.getSupplierId());
        if (stockItem.getDestId() != -1) {
            values.put(StockTable.DEST_ID, stockItem.getDestId());
        }
        values.put(StockTable.MASS, stockItem.getMass());
        if (stockItem.getNumBoxes() != -1) {
            values.put(StockTable.NUM_BOXES, stockItem.getNumBoxes());
        }
        values.put(StockTable.QUALITY, stockItem.getQuality().name());

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(StockTable.TABLE_NAME, null, values);
        db.close();
        return (int) newRowId;
    }

    /**
     * Updates the values of the {@link StockItem} in the database with the
     * values from the given object.
     */
    public boolean updateStockItem(StockItem stockItem) {
        ContentValues values = new ContentValues();
        values.put(StockTable.PRODUCT_ID, stockItem.getProduct().getProductId());
        values.put(StockTable.LOCATION_ID, stockItem.getLocationId());
        values.put(StockTable.SUPPLIER_ID, stockItem.getSupplierId());
        if (stockItem.getDestId() != -1) {
            values.put(StockTable.DEST_ID, stockItem.getDestId());
        }
        values.put(StockTable.MASS, stockItem.getMass());
        if (stockItem.getNumBoxes() != -1) {
            values.put(StockTable.NUM_BOXES, stockItem.getNumBoxes());
        }
        values.put(StockTable.QUALITY, stockItem.getQuality().name());

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(StockTable.TABLE_NAME, values,
                StockTable.ID + "=?", new String[]{stockItem.getStockId() + ""});
        db.close();
        return rowsAffected == 1;
    }

    /**
     * Removes the {@link StockItem} with this ID from the database if it exists.
     */
    public boolean deleteStockItem(int stockId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(StockTable.TABLE_NAME,
                StockTable.ID + "=?", new String[]{stockId + ""});
        db.close();
        return deletedRows == 1;
    }
    //endregion stock

    //region location

    /**
     * Queries the database for a {@link Location} with the specified ID.
     */
    public Location getLocation(int locationId) {
        Location locationResult = new Location();
        String query = "SELECT * FROM " + LocationsTable.TABLE_NAME
                + " WHERE " + LocationsTable.ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{locationId + ""});
        if (cursor.moveToFirst()) {
            locationResult.setLocationId(locationId);
            locationResult.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            String locationTypeString = cursor.getString(
                    cursor.getColumnIndexOrThrow(LocationsTable.TYPE));
            locationResult.setLocationType(
                    Location.LocationType.parseLocationType(locationTypeString));
            locationResult.setAddrLine1(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_1)));
            locationResult.setAddrLine2(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_2)));
            locationResult.setCity(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.CITY)));
            locationResult.setPostcode(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.POSTCODE)));
            locationResult.setCountry(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.COUNTRY)));
            locationResult.setPhone(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.PHONE)));
            locationResult.setEmail(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.EMAIL)));
        }
        cursor.close();
        db.close();
        return locationResult;
    }

    /**
     * Gets all locations in the database.
     */
    public List<Location> getAllLocations() {
        List<Location> locationResultList = new ArrayList<>();
        String query = "SELECT * FROM " + LocationsTable.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Location location = new Location();
            location.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            location.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            String locationTypeString = cursor.getString(
                    cursor.getColumnIndexOrThrow(LocationsTable.TYPE));
            location.setLocationType(
                    Location.LocationType.parseLocationType(locationTypeString));
            location.setAddrLine1(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_1)));
            location.setAddrLine2(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_2)));
            location.setCity(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.CITY)));
            location.setPostcode(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.POSTCODE)));
            location.setCountry(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.COUNTRY)));
            location.setPhone(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.PHONE)));
            location.setEmail(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.EMAIL)));
            locationResultList.add(location);
        }
        cursor.close();
        db.close();
        return locationResultList;
    }

    /**
     * Gets all locations of this type in the database.
     */
    public List<Location> getAllLocations(Location.LocationType locationType) {
        if (locationType == null) {
            return getAllLocations();
        }
        List<Location> locationResultList = new ArrayList<>();
        String query = "SELECT * FROM " + LocationsTable.TABLE_NAME
                + " WHERE " + LocationsTable.TYPE + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{locationType.name()});
        while (cursor.moveToNext()) {
            Location location = new Location();
            location.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            location.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            location.setLocationType(locationType);
            location.setAddrLine1(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_1)));
            location.setAddrLine2(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_2)));
            location.setCity(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.CITY)));
            location.setPostcode(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.POSTCODE)));
            location.setCountry(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.COUNTRY)));
            location.setPhone(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.PHONE)));
            location.setEmail(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.EMAIL)));
            locationResultList.add(location);
        }
        cursor.close();
        db.close();
        return locationResultList;
    }

    /**
     * Gets all storage locations that have stock.
     */
    public List<Location> getAllStorageLocationsWithStock() {
        List<Location> locationResultList = new ArrayList<>();
        String query = "SELECT * FROM " + LocationsTable.TABLE_NAME
                + " INNER JOIN " + StockTable.TABLE_NAME
                + " ON " + LocationsTable.TABLE_NAME + "." + LocationsTable.ID
                + "=" + StockTable.TABLE_NAME + "." + StockTable.LOCATION_ID
                + " GROUP BY " + LocationsTable.TABLE_NAME + "." + LocationsTable.ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Location location = new Location();
            location.setLocationId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable.ID)));
            location.setLocationName(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
            location.setLocationType(Location.LocationType.parseLocationType(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.TYPE))));
            location.setAddrLine1(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_1)));
            location.setAddrLine2(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.ADDR_2)));
            location.setCity(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.CITY)));
            location.setPostcode(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.POSTCODE)));
            location.setCountry(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.COUNTRY)));
            location.setPhone(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.PHONE)));
            location.setEmail(
                    cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.EMAIL)));
            locationResultList.add(location);
        }
        cursor.close();
        db.close();
        return locationResultList;
    }

    /**
     * Stores a {@link Location} in the database.
     */
    public int addLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(LocationsTable.NAME, location.getLocationName());
        values.put(LocationsTable.TYPE, location.getLocationType().name());
        values.put(LocationsTable.ADDR_1, location.getAddrLine1());
        values.put(LocationsTable.ADDR_2, location.getAddrLine2());
        values.put(LocationsTable.CITY, location.getCity());
        values.put(LocationsTable.POSTCODE, location.getPostcode());
        values.put(LocationsTable.COUNTRY, location.getCountry());
        values.put(LocationsTable.PHONE, location.getPhone());
        values.put(LocationsTable.EMAIL, location.getEmail());

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(LocationsTable.TABLE_NAME, null, values);
        db.close();
        return (int) newRowId;
    }

    /**
     * Updates the values of the {@link Location} in the database with the
     * values from the given object.
     */
    public boolean updateLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(LocationsTable.NAME, location.getLocationName());
        values.put(LocationsTable.ADDR_1, location.getAddrLine1());
        values.put(LocationsTable.ADDR_2, location.getAddrLine2());
        values.put(LocationsTable.CITY, location.getCity());
        values.put(LocationsTable.POSTCODE, location.getPostcode());
        values.put(LocationsTable.COUNTRY, location.getCountry());
        values.put(LocationsTable.PHONE, location.getPhone());
        values.put(LocationsTable.EMAIL, location.getEmail());

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(LocationsTable.TABLE_NAME, values,
                LocationsTable.ID + "=?",
                new String[]{location.getLocationId() + ""});
        db.close();
        return rowsAffected == 1;
    }

    /**
     * Queries the databases for any usages of the {@link Location} with this
     * ID in stock, orders or contracts. It is safe to delete if no usages
     * are found.
     */
    public boolean isLocationSafeToDelete(int locationId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String stockQuery = "SELECT * FROM " + StockTable.TABLE_NAME
                + " WHERE " + StockTable.LOCATION_ID + "=?"
                + " OR " + StockTable.SUPPLIER_ID + "=?"
                + " OR " + StockTable.DEST_ID + "=?";
        String ordersQuery = "SELECT * FROM " + OrdersTable.TABLE_NAME
                + " WHERE " + OrdersTable.DEST_ID + "=?";
        String contractsQuery = "SELECT * FROM " + ContractsTable.TABLE_NAME
                + " WHERE " + ContractsTable.DEST_ID + "=?";
        String locationIdStr = locationId + "";
        String[] stockSelectionArgs =
                new String[]{locationIdStr, locationIdStr, locationIdStr};
        String[] selectionArgs = new String[] {locationIdStr};
        boolean safeToDelete = true;
        for (String query : new String[]{stockQuery, ordersQuery, contractsQuery}) {
            if (!safeToDelete) {
                break;
            }
            Cursor cursor = db.rawQuery(query,
                    query.equals(stockQuery) ? stockSelectionArgs : selectionArgs);
            int numRows = cursor.getCount();
            safeToDelete = numRows == 0;
            cursor.close();
        }
        db.close();
        return safeToDelete;
    }

    /**
     * Removes the {@link Location} with this ID from the database if it exists.
     */
    public boolean deleteLocation(int locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(LocationsTable.TABLE_NAME,
                LocationsTable.ID + "=?", new String[]{locationId + ""});
        db.close();
        return deletedRows == 1;
    }
    //endregion location

    //region order

    /**
     * Queries the database for a {@link Order} with the specified ID.
     */
    public Order getOrder(int orderId) {
        Order orderResult = new Order();
        String query = "SELECT "
                + OrdersTable.TABLE_NAME + ".*,"
                + ProductsTable.TABLE_NAME + ".*,"
                + OrderProductsTable.QUANTITY_MASS + ","
                + OrderProductsTable.QUANTITY_BOXES + ","
                + LocationsTable.NAME
                + " FROM " + OrdersTable.TABLE_NAME
                + " INNER JOIN " + OrderProductsTable.TABLE_NAME + " ON "
                + OrdersTable.TABLE_NAME + "." + OrdersTable.ID
                + "=" + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.ORDER_ID
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " ON "
                + OrdersTable.TABLE_NAME + "." + OrdersTable.DEST_ID
                + "=" + LocationsTable.TABLE_NAME + "." + LocationsTable.ID
                + " WHERE " + OrdersTable.TABLE_NAME + "." + OrdersTable.ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{orderId + ""});
        boolean gotOrderData = false;
        while (cursor.moveToNext()) {
            // only query data about an order once to be more efficient
            if (!gotOrderData) {
                orderResult.setOrderId(orderId);
                orderResult.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.DEST_ID)));
                orderResult.setDestName(
                        cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
                orderResult.setOrderDate(Instant.ofEpochSecond(
                        cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.ORDER_DATE)))
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
                orderResult.setCompleted(
                        cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.COMPLETED))
                                == 1);
                gotOrderData = true;
            }

            // get data for each product attached to the order
            Product orderProduct = new Product();
            orderProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.ID)));
            orderProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            orderProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            double quantityMass = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.QUANTITY_BOXES));
            orderResult.addProduct(orderProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return orderResult;
    }

    /**
     * Queries the database for all completed / not completed orders.
     *
     * @param completed Whether to return completed or not completed orders.
     */
    public List<Order> getAllOrders(boolean completed) {
        List<Order> orderResultList = new ArrayList<>();
        String query = "SELECT "
                + OrdersTable.TABLE_NAME + ".*,"
                + ProductsTable.TABLE_NAME + ".*,"
                + OrderProductsTable.QUANTITY_MASS + ","
                + OrderProductsTable.QUANTITY_BOXES + ","
                + LocationsTable.NAME
                + " FROM " + OrdersTable.TABLE_NAME
                + " INNER JOIN " + OrderProductsTable.TABLE_NAME + " ON "
                + OrdersTable.TABLE_NAME + "." + OrdersTable.ID
                + "=" + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.ORDER_ID
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + OrderProductsTable.TABLE_NAME + "." + OrderProductsTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " ON "
                + OrdersTable.TABLE_NAME + "." + OrdersTable.DEST_ID
                + "=" + LocationsTable.TABLE_NAME + "." + LocationsTable.ID
                + " WHERE " + OrdersTable.COMPLETED + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{completed ? "1" : "0"});
        int lastOrderId = -1;
        while (cursor.moveToNext()) {
            int thisOrderId = cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.ID));
            // if new order rather than just extra product
            if (thisOrderId != lastOrderId) {
                Order order = new Order();
                order.setOrderId(thisOrderId);
                order.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.DEST_ID)));
                order.setDestName(
                        cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
                order.setOrderDate(Instant.ofEpochSecond(
                        cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.ORDER_DATE)))
                        .atZone(ZoneId.systemDefault()).toLocalDateTime());
                order.setCompleted(
                        cursor.getInt(cursor.getColumnIndexOrThrow(OrdersTable.COMPLETED))
                                == 1);
                orderResultList.add(order);
                lastOrderId = thisOrderId;
            }

            // get order object for current db row
            Order thisOrder = orderResultList.get(orderResultList.size() - 1);
            // get data for each product attached to the order
            Product orderProduct = new Product();
            orderProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.ID)));
            orderProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            orderProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            double quantityMass = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(
                    cursor.getColumnIndexOrThrow(OrderProductsTable.QUANTITY_BOXES));
            thisOrder.addProduct(orderProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return orderResultList;
    }

    /**
     * Gets all orders in the database that are not completed.
     */
    public List<Order> getAllOrdersNotCompleted() {
        return getAllOrders(false);
    }

    /**
     * Gets all orders in the database that are completed.
     */
    public List<Order> getAllOrdersCompleted() {
        return getAllOrders(true);
    }

    /**
     * Stores an {@link Order} in the database.
     */
    public int addOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put(OrdersTable.DEST_ID, order.getDestId());
        values.put(OrdersTable.ORDER_DATE,
                order.getOrderDate().atZone(ZoneId.systemDefault()).toEpochSecond());
        values.put(OrdersTable.COMPLETED, order.isCompleted() ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(OrdersTable.TABLE_NAME, null, values);

        // insert each product linked to the order into OrderProducts table
        for (ProductQuantity productQuantity : order.getProductList()) {
            ContentValues productValues = new ContentValues();
            productValues.put(OrderProductsTable.PRODUCT_ID,
                    productQuantity.getProduct().getProductId());
            productValues.put(OrderProductsTable.ORDER_ID, newRowId);
            productValues.put(OrderProductsTable.QUANTITY_MASS,
                    productQuantity.getQuantityMass());
            productValues.put(OrderProductsTable.QUANTITY_BOXES,
                    productQuantity.getQuantityBoxes());
            db.insert(OrderProductsTable.TABLE_NAME, null, productValues);
        }
        db.close();
        return (int) newRowId;
    }

    /**
     * Updates the values of the {@link Order} in the database with the values
     * from the given object.
     */
    public boolean updateOrder(Order order) {
        ContentValues values = new ContentValues();
        values.put(OrdersTable.DEST_ID, order.getDestId());
        values.put(OrdersTable.ORDER_DATE,
                order.getOrderDate().atZone(ZoneId.systemDefault()).toEpochSecond());
        values.put(OrdersTable.COMPLETED, order.isCompleted() ? 1 : 0);

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(OrdersTable.TABLE_NAME, values,
                OrdersTable.ID + "=?", new String[]{order.getOrderId() + ""});

        //delete products in db for order, then re-add the updated ones
        db.delete(OrderProductsTable.TABLE_NAME, OrderProductsTable.ORDER_ID + "=?",
                new String[]{order.getOrderId() + ""});

        for (ProductQuantity productQuantity : order.getProductList()) {
            ContentValues productValues = new ContentValues();
            productValues.put(OrderProductsTable.PRODUCT_ID,
                    productQuantity.getProduct().getProductId());
            productValues.put(OrderProductsTable.ORDER_ID, order.getOrderId());
            productValues.put(OrderProductsTable.QUANTITY_MASS,
                    productQuantity.getQuantityMass());
            productValues.put(OrderProductsTable.QUANTITY_BOXES,
                    productQuantity.getQuantityBoxes());
            db.insert(OrderProductsTable.TABLE_NAME, null, productValues);
        }
        db.close();
        return rowsAffected == 1;
    }

    /**
     * Removes the {@link Order} with this ID from the database if it exists.
     */
    public boolean deleteOrder(int orderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(OrdersTable.TABLE_NAME,
                OrdersTable.ID + "=?", new String[]{orderId + ""});
        db.delete(OrderProductsTable.TABLE_NAME,
                OrderProductsTable.ORDER_ID + "=?", new String[]{orderId + ""});
        db.close();
        return deletedRows == 1;
    }
    //endregion order

    //region contract

    /**
     * Queries the database for a {@link Contract} with the specified ID.
     */
    public Contract getContract(int contractId) {
        Contract contractResult = new Contract();
        String query = "SELECT "
                + ContractsTable.TABLE_NAME + ".*,"
                + ProductsTable.TABLE_NAME + ".*,"
                + ContractProductsTable.QUANTITY_MASS + ","
                + ContractProductsTable.QUANTITY_BOXES + ","
                + LocationsTable.NAME
                + " FROM " + ContractsTable.TABLE_NAME
                + " INNER JOIN " + ContractProductsTable.TABLE_NAME + " ON "
                + ContractsTable.TABLE_NAME + "." + ContractsTable.ID
                + "=" + ContractProductsTable.TABLE_NAME
                + "." + ContractProductsTable.CONTRACT_ID
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + ContractProductsTable.TABLE_NAME + "." + ContractProductsTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " ON "
                + ContractsTable.TABLE_NAME + "." + ContractsTable.DEST_ID
                + "=" + LocationsTable.TABLE_NAME + "." + LocationsTable.ID
                + " WHERE " + ContractsTable.TABLE_NAME + "." + ContractsTable.ID + "=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{contractId + ""});
        boolean gotContractData = false;
        while (cursor.moveToNext()) {
            // only query contract data once
            if (!gotContractData) {
                contractResult.setContractId(contractId);
                contractResult.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContractsTable.DEST_ID)));
                contractResult.setDestName(
                        cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
                Interval repeatInterval = new Interval(
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                ContractsTable.REPEAT_INTERVAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                ContractsTable.REPEAT_INTERVAL_UNIT))
                );
                contractResult.setRepeatInterval(repeatInterval);
                contractResult.setRepeatOn(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContractsTable.REPEAT_ON)));
                // get start date as a LocalDate object from Epoch days
                contractResult.setStartDate(LocalDate.ofEpochDay(cursor.getInt(
                        cursor.getColumnIndexOrThrow(ContractsTable.START_DATE))));
                contractResult.setReminder(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContractsTable.REMINDER)));
                gotContractData = true;
            }

            // get data for each product in the contract
            Product contractProduct = new Product();
            contractProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.ID)));
            contractProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            contractProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            double quantityMass = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.QUANTITY_BOXES));
            contractResult.addProduct(contractProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return contractResult;
    }

    /**
     * Gets all contracts in the database.
     */
    public List<Contract> getAllContracts() {
        List<Contract> contractResultList = new ArrayList<>();
        String query = "SELECT "
                + ContractsTable.TABLE_NAME + ".*,"
                + ProductsTable.TABLE_NAME + ".*,"
                + ContractProductsTable.QUANTITY_MASS + ","
                + ContractProductsTable.QUANTITY_BOXES + ","
                + LocationsTable.NAME
                + " FROM " + ContractsTable.TABLE_NAME
                + " INNER JOIN " + ContractProductsTable.TABLE_NAME + " ON "
                + ContractsTable.TABLE_NAME + "." + ContractsTable.ID
                + "=" + ContractProductsTable.TABLE_NAME
                + "." + ContractProductsTable.CONTRACT_ID
                + " INNER JOIN " + ProductsTable.TABLE_NAME + " ON "
                + ContractProductsTable.TABLE_NAME + "." + ContractProductsTable.PRODUCT_ID
                + "=" + ProductsTable.TABLE_NAME + "." + ProductsTable.ID
                + " INNER JOIN " + LocationsTable.TABLE_NAME + " ON "
                + ContractsTable.TABLE_NAME + "." + ContractsTable.DEST_ID
                + "=" + LocationsTable.TABLE_NAME + "." + LocationsTable.ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int lastContractId = -1;
        while (cursor.moveToNext()) {
            int thisContractId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContractsTable.ID));
            // if new contract
            if (thisContractId != lastContractId) {
                Contract contract = new Contract();
                contract.setContractId(thisContractId);
                contract.setDestId(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContractsTable.DEST_ID)));
                contract.setDestName(
                        cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.NAME)));
                Interval repeatInterval = new Interval(
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                ContractsTable.REPEAT_INTERVAL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(
                                ContractsTable.REPEAT_INTERVAL_UNIT))
                );
                contract.setRepeatInterval(repeatInterval);
                contract.setRepeatOn(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContractsTable.REPEAT_ON)));
                contract.setStartDate(LocalDate.ofEpochDay(
                        cursor.getInt(cursor.getColumnIndexOrThrow(
                                ContractsTable.START_DATE))));
                contract.setReminder(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ContractsTable.REMINDER)));
                contractResultList.add(contract);
                lastContractId = thisContractId;
            }

            // get contract object for current row
            Contract thisContract = contractResultList.get(contractResultList.size() - 1);
            // get data for each product in the contract
            Product contractProduct = new Product();
            contractProduct.setProductId(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ProductsTable.ID)));
            contractProduct.setProductName(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.NAME)));
            contractProduct.setMeatType(
                    cursor.getString(cursor.getColumnIndexOrThrow(ProductsTable.MEAT_TYPE)));
            double quantityMass = cursor.getDouble(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContractProductsTable.QUANTITY_BOXES));
            thisContract.addProduct(contractProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return contractResultList;
    }

    /**
     * Stores a {@link Contract} in the database.
     */
    public int addContract(Contract contract) {
        ContentValues values = new ContentValues();
        values.put(ContractsTable.DEST_ID, contract.getDestId());
        values.put(ContractsTable.REPEAT_INTERVAL, contract.getRepeatInterval().getValue());
        values.put(ContractsTable.REPEAT_INTERVAL_UNIT,
                contract.getRepeatInterval().getUnit().name());
        values.put(ContractsTable.REPEAT_ON, contract.getRepeatOn());
        if (contract.getStartDate() == null) contract.setStartDate(LocalDate.now());
        values.put(ContractsTable.START_DATE, contract.getStartDate().toEpochDay());
        values.put(ContractsTable.REMINDER, contract.getReminder());

        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = db.insert(ContractsTable.TABLE_NAME, null, values);

        for (ProductQuantity productQuantity : contract.getProductList()) {
            ContentValues productValues = new ContentValues();
            productValues.put(ContractProductsTable.PRODUCT_ID,
                    productQuantity.getProduct().getProductId());
            productValues.put(ContractProductsTable.CONTRACT_ID, newRowId);
            productValues.put(ContractProductsTable.QUANTITY_MASS,
                    productQuantity.getQuantityMass());
            productValues.put(ContractProductsTable.QUANTITY_BOXES,
                    productQuantity.getQuantityBoxes());
            db.insert(ContractProductsTable.TABLE_NAME, null, productValues);
        }
        db.close();
        return (int) newRowId;
    }

    /**
     * Updates the values of the {@link Contract} in the database with the
     * values from the given object.
     */
    public boolean updateContract(Contract contract) {
        ContentValues values = new ContentValues();
        values.put(ContractsTable.DEST_ID, contract.getDestId());
        values.put(ContractsTable.REPEAT_INTERVAL, contract.getRepeatInterval().getValue());
        values.put(ContractsTable.REPEAT_INTERVAL_UNIT,
                contract.getRepeatInterval().getUnit().name());
        values.put(ContractsTable.REPEAT_ON, contract.getRepeatOn());
        values.put(ContractsTable.START_DATE, contract.getStartDate().toEpochDay());
        values.put(ContractsTable.REMINDER, contract.getReminder());

        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.update(ContractsTable.TABLE_NAME, values,
                ContractsTable.ID + "=?", new String[]{contract.getContractId() + ""});

        db.delete(ContractProductsTable.TABLE_NAME, ContractProductsTable.CONTRACT_ID + "=?",
                new String[]{contract.getContractId() + ""});

        for (ProductQuantity productQuantity : contract.getProductList()) {
            ContentValues productValues = new ContentValues();
            productValues.put(ContractProductsTable.PRODUCT_ID,
                    productQuantity.getProduct().getProductId());
            productValues.put(ContractProductsTable.CONTRACT_ID, contract.getContractId());
            productValues.put(ContractProductsTable.QUANTITY_MASS,
                    productQuantity.getQuantityMass());
            productValues.put(ContractProductsTable.QUANTITY_BOXES,
                    productQuantity.getQuantityBoxes());
            db.insert(ContractProductsTable.TABLE_NAME, null, productValues);
        }
        db.close();
        return rowsAffected == 1;
    }

    /**
     * Removes the {@link Contract} with this ID from the database if it exists.
     */
    public boolean deleteContract(int contractId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(ContractsTable.TABLE_NAME,
                ContractsTable.ID + "=?", new String[]{contractId + ""});
        db.delete(ContractProductsTable.TABLE_NAME,
                ContractProductsTable.CONTRACT_ID + "=?", new String[]{contractId + ""});
        db.close();
        return deletedRows == 1;
    }
    //endregion contract
}
