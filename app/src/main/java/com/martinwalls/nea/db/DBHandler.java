package com.martinwalls.nea.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.martinwalls.nea.data.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class DBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "stockDB.db";
    private static final int DATABASE_VERSION = 1;

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
    private final String STOCK_ID = "StockId";
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
                + PRODUCTS_NAME + " TEXT NOT NULL UNIQUE, "
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
                + STOCK_ID + " INTEGER PRIMARY KEY,"
                + STOCK_PRODUCT_ID + " INTEGER NOT NULL, "
                + STOCK_LOCATION_ID + " INTEGER NOT NULL, "
                + STOCK_SUPPLIER_ID + " INTEGER NOT NULL, "
                + STOCK_DEST_ID + " INTEGER, "
                + STOCK_MASS + " REAL NOT NULL, "
                + STOCK_NUM_BOXES + " REAL, "
                + STOCK_QUALITY + " TEXT NOT NULL, "
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
                + ORDER_PRODUCTS_QUANTITY_BOXES + " INTEGER, "
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
                + CONTRACT_PRODUCTS_QUANTITY_BOXES + " INTEGER, "
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

    //todo finish dbHandler getters
    public Product getProduct(int productId) {
        Product productResult = new Product();
        String query = "SELECT * FROM " + TABLE_PRODUCTS
                + " WHERE " + PRODUCTS_ID + " = " + productId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            productResult.setProductId(productId);
            productResult.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_NAME)));
            productResult.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_MEAT_TYPE)));
        }
        cursor.close();
        db.close();
        return productResult;
    }

    public List<Product> getAllProducts() {
        List<Product> productResultList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Product product = new Product();
            product.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_ID)));
            product.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_NAME)));
            product.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_MEAT_TYPE)));
            productResultList.add(product);
        }
        cursor.close();
        db.close();
        return productResultList;
    }

    public List<String> getAllMeatTypes() {
        List<String> meatTypeResultList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_MEAT_TYPES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            meatTypeResultList.add(cursor.getString(cursor.getColumnIndexOrThrow(MEAT_TYPES_TYPE)));
        }
        cursor.close();
        db.close();
        return meatTypeResultList;
    }

    public StockItem getStockItem(int stockId) {
        // TODO check for null values
        StockItem stockResult = new StockItem();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT " + STOCK_ID + "," + TABLE_STOCK + "." + STOCK_PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LOCATIONS_ID + ","
                + ALIAS_LOCATION + "." + LOCATIONS_NAME + ","
                + ALIAS_SUPPLIER + "." + LOCATIONS_ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LOCATIONS_NAME + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LOCATIONS_ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LOCATIONS_NAME + " AS " + ALIAS_DEST_NAME + ","
                + STOCK_MASS + "," + STOCK_NUM_BOXES + "," + STOCK_QUALITY + ","
                + TABLE_PRODUCTS + "." + PRODUCTS_NAME + "," + TABLE_PRODUCTS + "." + PRODUCTS_MEAT_TYPE
                + " FROM " + TABLE_STOCK
                + " INNER JOIN " + TABLE_PRODUCTS + " ON "
                + TABLE_STOCK + "." + STOCK_PRODUCT_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " AS " + ALIAS_LOCATION + " ON "
                + TABLE_STOCK + "." + STOCK_LOCATION_ID + "=" + ALIAS_LOCATION + "." + LOCATIONS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " AS " + ALIAS_SUPPLIER + " ON "
                + TABLE_STOCK + "." + STOCK_SUPPLIER_ID + "=" + ALIAS_SUPPLIER + "." + LOCATIONS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " AS " + ALIAS_DEST + " ON "
                + TABLE_STOCK + "." + STOCK_DEST_ID + "=" + ALIAS_DEST + "." + LOCATIONS_ID
                + " WHERE " + STOCK_ID + "=" + stockId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_STOCK + "." + STOCK_PRODUCT_ID))); // Stock.ProductId
            stockProduct.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_PRODUCTS + "." + PRODUCTS_NAME))); // Products.ProductName
            stockProduct.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_PRODUCTS + "." + PRODUCTS_MEAT_TYPE))); // Products.MeatType
            stockResult.setProduct(stockProduct);
            stockResult.setLocationId(cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_LOCATION + "." + LOCATIONS_ID)));
            stockResult.setLocationName(cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_LOCATION + "." + LOCATIONS_NAME)));
            stockResult.setSupplierId(cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockResult.setSupplierName(cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            stockResult.setDestId(cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            stockResult.setDestName(cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockResult.setMass(cursor.getDouble(cursor.getColumnIndexOrThrow(STOCK_MASS)));
            stockResult.setNumBoxes(cursor.getInt(cursor.getColumnIndexOrThrow(STOCK_NUM_BOXES)));
            stockResult.setQuality(StockItem.Quality.parseQuality(cursor.getString(cursor.getColumnIndexOrThrow(STOCK_QUALITY))));
        }
        cursor.close();
        db.close();
        return stockResult;
    }

    public List<StockItem> getAllStock() {
        List<StockItem> stockResultList = new ArrayList<>();
        final String ALIAS_LOCATION = "Location";
        final String ALIAS_SUPPLIER = "Supplier";
        final String ALIAS_SUPPLIER_ID = "SupplierId";
        final String ALIAS_SUPPLIER_NAME = "SupplierName";
        final String ALIAS_DEST = "Dest";
        final String ALIAS_DEST_ID = "DestId";
        final String ALIAS_DEST_NAME = "DestName";
        String query = "SELECT " + STOCK_ID + "," + TABLE_STOCK + "." + STOCK_PRODUCT_ID + ","
                + ALIAS_LOCATION + "." + LOCATIONS_ID + ","
                + ALIAS_LOCATION + "." + LOCATIONS_NAME + ","
                + ALIAS_SUPPLIER + "." + LOCATIONS_ID + " AS " + ALIAS_SUPPLIER_ID + ","
                + ALIAS_SUPPLIER + "." + LOCATIONS_NAME + " AS " + ALIAS_SUPPLIER_NAME + ","
                + ALIAS_DEST + "." + LOCATIONS_ID + " AS " + ALIAS_DEST_ID + ","
                + ALIAS_DEST + "." + LOCATIONS_NAME + " AS " + ALIAS_DEST_NAME + ","
                + STOCK_MASS + "," + STOCK_NUM_BOXES + "," + STOCK_QUALITY + ","
                + TABLE_PRODUCTS + "." + PRODUCTS_NAME + "," + TABLE_PRODUCTS + "." + PRODUCTS_MEAT_TYPE
                + " FROM " + TABLE_STOCK
                + " INNER JOIN " + TABLE_PRODUCTS + " ON "
                + TABLE_STOCK + "." + STOCK_PRODUCT_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " AS " + ALIAS_LOCATION + " ON "
                + TABLE_STOCK + "." + STOCK_LOCATION_ID + "=" + ALIAS_LOCATION + "." + LOCATIONS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " AS " + ALIAS_SUPPLIER + " ON "
                + TABLE_STOCK + "." + STOCK_SUPPLIER_ID + "=" + ALIAS_SUPPLIER + "." + LOCATIONS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " AS " + ALIAS_DEST + " ON "
                + TABLE_STOCK + "." + STOCK_DEST_ID + "=" + ALIAS_DEST + "." + LOCATIONS_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            StockItem stockItem = new StockItem();
            // get product data from inner join
            Product stockProduct = new Product();
            stockProduct.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_STOCK + "." + STOCK_PRODUCT_ID))); // Stock.ProductId
            stockProduct.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_PRODUCTS + "." + PRODUCTS_NAME))); // Products.ProductName
            stockProduct.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_PRODUCTS + "." + PRODUCTS_MEAT_TYPE))); // Products.MeatType
            stockItem.setProduct(stockProduct);
            stockItem.setLocationId(cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_LOCATION + "." + LOCATIONS_ID)));
            stockItem.setLocationName(cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_LOCATION + "." + LOCATIONS_NAME)));
            stockItem.setSupplierId(cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_ID)));
            stockItem.setSupplierName(cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_SUPPLIER_NAME)));
            stockItem.setDestId(cursor.getInt(cursor.getColumnIndexOrThrow(ALIAS_DEST_ID)));
            stockItem.setDestName(cursor.getString(cursor.getColumnIndexOrThrow(ALIAS_DEST_NAME)));
            stockItem.setMass(cursor.getDouble(cursor.getColumnIndexOrThrow(STOCK_MASS)));
            stockItem.setNumBoxes(cursor.getInt(cursor.getColumnIndexOrThrow(STOCK_NUM_BOXES)));
            stockItem.setQuality(StockItem.Quality.parseQuality(cursor.getString(cursor.getColumnIndexOrThrow(STOCK_QUALITY))));
            stockResultList.add(stockItem);
        }
        cursor.close();
        db.close();
        return stockResultList;
    }

    public Location getLocation(int locationId) {
        Location locationResult = new Location();
        String query = "SELECT * FROM " + TABLE_LOCATIONS
                + " WHERE " + LOCATIONS_ID + " = " + locationId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            locationResult.setLocationId(locationId);
            locationResult.setLocationName(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_NAME)));
            String locationTypeString = cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_NAME));
            for (Location.LocationType type : Location.LocationType.values()) {
                if (type.name().equalsIgnoreCase(locationTypeString)) {
                    locationResult.setLocationType(type);
                }
            }
            locationResult.setAddrLine1(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_ADDR_1)));
            locationResult.setAddrLine2(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_ADDR_2)));
            locationResult.setCity(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_CITY)));
            locationResult.setPostcode(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_POSTCODE)));
            locationResult.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_COUNTRY)));
            locationResult.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_PHONE)));
            locationResult.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_EMAIL)));
        }
        cursor.close();
        db.close();
        return locationResult;
    }

    public List<Location> getAllLocations() {
        List<Location> locationResultList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            Location location = new Location();
            location.setLocationId(cursor.getInt(cursor.getColumnIndexOrThrow(LOCATIONS_ID)));
            location.setLocationName(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_NAME)));
            String locationTypeString = cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_NAME));
            for (Location.LocationType type : Location.LocationType.values()) {
                if (type.name().equalsIgnoreCase(locationTypeString)) {
                    location.setLocationType(type);
                }
            }
            location.setAddrLine1(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_ADDR_1)));
            location.setAddrLine2(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_ADDR_2)));
            location.setCity(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_CITY)));
            location.setPostcode(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_POSTCODE)));
            location.setCountry(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_COUNTRY)));
            location.setPhone(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_PHONE)));
            location.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(LOCATIONS_EMAIL)));
            locationResultList.add(location);
        }
        cursor.close();
        db.close();
        return locationResultList;
    }

    public Order getOrder(int orderId) {
        Order orderResult = new Order();
        String query = "SELECT " + TABLE_ORDERS + ".*," + TABLE_PRODUCTS + ".*,"
                + ORDER_PRODUCTS_QUANTITY_MASS + "," + ORDER_PRODUCTS_QUANTITY_BOXES + "," + LOCATIONS_NAME
                + " FROM " + TABLE_ORDERS
                + " INNER JOIN " + TABLE_ORDER_PRODUCTS + " ON "
                + TABLE_ORDERS + "." + ORDERS_ID + "=" + TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_ORDER_ID
                + " INNER JOIN " + TABLE_PRODUCTS + " ON "
                + TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_PRODUCT_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " ON "
                + TABLE_ORDERS + "." + ORDERS_DEST_ID + "=" + TABLE_LOCATIONS + "." + LOCATIONS_ID
                + " WHERE " + TABLE_ORDERS + "." + ORDERS_ID + "=" + orderId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean gotOrderData = false;
        while (cursor.moveToNext()) {
            // only query data about an order once to be more efficient
            if (!gotOrderData) {
                orderResult.setOrderId(orderId);
                orderResult.setDestId(cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_DEST_ID)));
                orderResult.setDestName(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_LOCATIONS + "." + LOCATIONS_NAME)));
                orderResult.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DATE)));
                orderResult.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_COMPLETED)) == 1);
                gotOrderData = true;
            }

            // get data for each product attached to the order
            Product orderProduct = new Product();
            orderProduct.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_ID)));
            orderProduct.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_NAME)));
            orderProduct.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_MEAT_TYPE)));
            double quantityMass = cursor.getDouble(cursor.getColumnIndexOrThrow(TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_BOXES)); //todo handle if this is null
            orderResult.addProduct(orderProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return orderResult;
    }

    public List<Order> getAllOrders() {
        List<Order> orderResultList = new ArrayList<>();
        String query = "SELECT " + TABLE_ORDERS + ".*," + TABLE_PRODUCTS + ".*,"
                + ORDER_PRODUCTS_QUANTITY_MASS + "," + ORDER_PRODUCTS_QUANTITY_BOXES + "," + LOCATIONS_NAME
                + " FROM " + TABLE_ORDERS
                + " INNER JOIN " + TABLE_ORDER_PRODUCTS + " ON "
                + TABLE_ORDERS + "." + ORDERS_ID + "=" + TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_ORDER_ID
                + " INNER JOIN " + TABLE_PRODUCTS + " ON "
                + TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_PRODUCT_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " ON "
                + TABLE_ORDERS + "." + ORDERS_DEST_ID + "=" + TABLE_LOCATIONS + "." + LOCATIONS_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int lastOrderId = -1;
        while (cursor.moveToNext()) {
            int thisOrderId = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_ORDERS + "." + ORDERS_ID));
            // if new order rather than just extra product
            if (thisOrderId != lastOrderId) {
                Order order = new Order();
                order.setOrderId(thisOrderId);
                order.setDestId(cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_DEST_ID)));
                order.setDestName(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_LOCATIONS + "." + LOCATIONS_NAME)));
                order.setOrderDate(cursor.getString(cursor.getColumnIndexOrThrow(ORDERS_DATE)));
                order.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(ORDERS_COMPLETED)) == 1);
                orderResultList.add(order);
            }

            // get order object for current db row
            Order thisOrder = orderResultList.get(orderResultList.size() - 1);
            // get data for each product attached to the order
            Product orderProduct = new Product();
            orderProduct.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_ID)));
            orderProduct.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_NAME)));
            orderProduct.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_MEAT_TYPE)));
            double quantityMass = cursor.getDouble(cursor.getColumnIndexOrThrow(TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_ORDER_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_BOXES));
            thisOrder.addProduct(orderProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return orderResultList;
    }

    public Contract getContract(int contractId) {
        Contract contractResult = new Contract();
        String query = "SELECT " + TABLE_CONTRACTS + ".*," + TABLE_PRODUCTS + ".*,"
                + ORDER_PRODUCTS_QUANTITY_MASS + "," + ORDER_PRODUCTS_QUANTITY_BOXES
                + " FROM " + TABLE_CONTRACTS
                + " INNER JOIN " + TABLE_CONTRACT_PRODUCTS + " ON "
                + TABLE_CONTRACTS + "." + CONTRACTS_ID + "=" + TABLE_CONTRACT_PRODUCTS + "." + CONTRACT_PRODUCTS_CONTRACT_ID
                + " INNER JOIN " + TABLE_PRODUCTS + " ON "
                + TABLE_CONTRACT_PRODUCTS + "." + CONTRACT_PRODUCTS_PRODUCT_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " ON "
                + TABLE_CONTRACTS + "." + CONTRACTS_DEST_ID + "=" + TABLE_LOCATIONS + "." + LOCATIONS_ID
                + " WHERE " + TABLE_CONTRACTS + "." + CONTRACTS_ID + "=" + contractId;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        boolean gotContractData = false;
        while (cursor.moveToNext()) {
            // only query contract data once
            if (!gotContractData) {
                contractResult.setContractId(contractId);
                contractResult.setDestId(cursor.getInt(cursor.getColumnIndexOrThrow(CONTRACTS_DEST_ID)));
                contractResult.setDestName(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_LOCATIONS + "." + LOCATIONS_NAME)));
                contractResult.setRepeatInterval(cursor.getString(cursor.getColumnIndexOrThrow(CONTRACTS_REPEAT_INTERVAL)));
                contractResult.setRepeatOn(cursor.getString(cursor.getColumnIndexOrThrow(CONTRACTS_REPEAT_ON)));
                contractResult.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow(CONTRACTS_REMINDER)));
                gotContractData = true;
            }

            // get data for each product in the contract
            Product contractProduct = new Product();
            contractProduct.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_ID)));
            contractProduct.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_NAME)));
            contractProduct.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_MEAT_TYPE)));
            double quantityMass = cursor.getDouble(cursor.getColumnIndexOrThrow(TABLE_CONTRACT_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_CONTRACT_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_BOXES));
            contractResult.addProduct(contractProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return contractResult;
    }

    public List<Contract> getAllContracts() {
        List<Contract> contractResultList = new ArrayList<>();
        //todo refactor these queries into global variables, for getting a single order just append the WHERE clause - eliminate repetition
        String query = "SELECT " + TABLE_CONTRACTS + ".*," + TABLE_PRODUCTS + ".*,"
                + ORDER_PRODUCTS_QUANTITY_MASS + "," + ORDER_PRODUCTS_QUANTITY_BOXES
                + " FROM " + TABLE_CONTRACTS
                + " INNER JOIN " + TABLE_CONTRACT_PRODUCTS + " ON "
                + TABLE_CONTRACTS + "." + CONTRACTS_ID + "=" + TABLE_CONTRACT_PRODUCTS + "." + CONTRACT_PRODUCTS_CONTRACT_ID
                + " INNER JOIN " + TABLE_PRODUCTS + " ON "
                + TABLE_CONTRACT_PRODUCTS + "." + CONTRACT_PRODUCTS_PRODUCT_ID + "=" + TABLE_PRODUCTS + "." + PRODUCTS_ID
                + " INNER JOIN " + TABLE_LOCATIONS + " ON "
                + TABLE_CONTRACTS + "." + CONTRACTS_DEST_ID + "=" + TABLE_LOCATIONS + "." + LOCATIONS_ID;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        int lastContractId = -1;
        while (cursor.moveToNext()) {
            int thisContractId = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_CONTRACTS + "." + CONTRACTS_ID));
            // if new contract
            if (thisContractId != lastContractId) {
                Contract contract = new Contract();
                contract.setContractId(thisContractId);
                contract.setDestId(cursor.getInt(cursor.getColumnIndexOrThrow(CONTRACTS_DEST_ID)));
                contract.setDestName(cursor.getString(cursor.getColumnIndexOrThrow(TABLE_LOCATIONS + "." + LOCATIONS_NAME)));
                contract.setRepeatInterval(cursor.getString(cursor.getColumnIndexOrThrow(CONTRACTS_REPEAT_INTERVAL)));
                contract.setRepeatOn(cursor.getString(cursor.getColumnIndexOrThrow(CONTRACTS_REPEAT_ON)));
                contract.setReminder(cursor.getInt(cursor.getColumnIndexOrThrow(CONTRACTS_REMINDER)));
                contractResultList.add(contract);
            }

            // get contract object for current row
            Contract thisContract = contractResultList.get(contractResultList.size() - 1);
            // get data for each product in the contract
            Product contractProduct = new Product();
            contractProduct.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(PRODUCTS_ID)));
            contractProduct.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_NAME)));
            contractProduct.setMeatType(cursor.getString(cursor.getColumnIndexOrThrow(PRODUCTS_MEAT_TYPE)));
            double quantityMass = cursor.getDouble(cursor.getColumnIndexOrThrow(TABLE_CONTRACT_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_MASS));
            int quantityBoxes = cursor.getInt(cursor.getColumnIndexOrThrow(TABLE_CONTRACT_PRODUCTS + "." + ORDER_PRODUCTS_QUANTITY_BOXES));
            thisContract.addProduct(contractProduct, quantityMass, quantityBoxes);
        }
        cursor.close();
        db.close();
        return contractResultList;
    }

    //todo dbHandler setters
    public boolean addStockItem(StockItem stockItem) {
        ContentValues values = new ContentValues();
        values.put(STOCK_PRODUCT_ID, stockItem.productId);
        values.put(STOCK_LOCATION_ID, stockItem.locationId);
        values.put(STOCK_SUPPLIER_ID, stockItem.supplierId);
        values.put(STOCK_DEST_ID, stockItem.destId);
        values.put(STOCK_MASS, stockItem.mass);
        values.put(STOCK_NUM_BOXES, stockItem.numBoxes);
        values.put(STOCK_QUALITY, stockItem.quality);

        SQLiteDatabase db = this.getWritableDatabase();
        // id of inserted row, -1 if error
        long newRowId = db.insert(TABLE_STOCK, null, values);
        db.close();
        return newRowId != -1;
    }

    //todo backup db
}
