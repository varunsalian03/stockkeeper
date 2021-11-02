package com.example.android.stockkeepingassistant.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.stockkeepingassistant.R;
import com.example.android.stockkeepingassistant.model.ProductContract.ProductEntry;

import java.util.Objects;

public class ProductProvider extends ContentProvider {

    public static final String TAG = ProductProvider.class.getSimpleName();

    private ProductDbHelper mDbHelper;

    private static final int PRODUCTS = 100;

    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private Context context;

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        context = Objects.requireNonNull(getContext());
        mDbHelper = new ProductDbHelper(context);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri,
                        @Nullable String[] projection,
                        @Nullable String selection,
                        @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case PRODUCT_ID:
                selection = ProductEntry.UUID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(
                        ProductContract.ProductEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Error: Unknown URI " + uri);
        }

        cursor.setNotificationUri(context.getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                String info = Objects.requireNonNull(getContext()).getString(
                        R.string.provider_type_mismatch_part1) +
                        uri +
                        getContext().getString(R.string.provider_type_mismatch_part2) +
                        match;

                throw new IllegalStateException(info);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException(context.getString(R.string.unsupported_insertion) + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        final int match = sUriMatcher.match(uri);


        int numRecordsDeleted;


        switch (match) {
            case PRODUCTS:

                numRecordsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (numRecordsDeleted != 0) {
                    context.getContentResolver().notifyChange(uri, null);
                }


                return numRecordsDeleted;
            case PRODUCT_ID:
                selection = ProductEntry.UUID + "=?";


                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};


                numRecordsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);

                if (numRecordsDeleted != 0) {
                    context.getContentResolver().notifyChange(uri, null);
                }


                return numRecordsDeleted;
            default:
                throw new IllegalArgumentException(
                        context.getString(R.string.delete_product_failure) + uri
                );
        }
    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues values,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);


        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry.UUID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(context.getString(R.string.update_product_failure) + uri);
        }
    }

    @Nullable
    private Uri insertProduct(Uri uri, @Nullable ContentValues contentValues) {
        if (contentValues == null) {
            return null;
        }

        String uuidString = contentValues.getAsString(ProductEntry.UUID);
        String title = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_TITLE);
        Integer quantity = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        String price = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
        String supplierName = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
        String supplierEmail = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);

        if (uuidString == null || uuidString.isEmpty()) {
            throw new IllegalArgumentException("Product class did not generate ID!");
        }

        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_name));
        }

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_quantity));
        }

        if (price == null || price.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_price));
        }

        if (supplierName == null || supplierName.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_supplier));
        }

        if (supplierEmail == null || supplierEmail.isEmpty()) {
            throw new IllegalArgumentException(context.getString(R.string.insert_product_no_supplier_contact));
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(TAG, context.getString(R.string.insert_failure) + uri);
            return null;
        }

        context.getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    private int updateProduct(Uri uri,
                              @Nullable ContentValues values,
                              @Nullable String selection,
                              @Nullable String[] selectionArgs) {
        if (values != null && values.size() != 0) {
            if (values.containsKey(ProductEntry.COLUMN_PRODUCT_TITLE)) {
                String title = values.getAsString(ProductEntry.COLUMN_PRODUCT_TITLE);
                if (title == null || title.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_desc));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
                Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
                if (quantity == null || quantity < 0) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_quantity));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
                String price = values.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
                if (price == null || price.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_price));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
                String supplier = values.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
                if (supplier == null || supplier.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_supplier));
                }
            } else if (values.containsKey(ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
                String contact = values.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);
                if (contact == null || contact.isEmpty()) {
                    throw new IllegalArgumentException(context.getString(R.string.update_product_no_supplier_contact));
                }
            }
        } else {
            return 0;
        }


        SQLiteDatabase db = mDbHelper.getWritableDatabase();


        int numRowsAffected = db.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        if (numRowsAffected != 0) {
            context.getContentResolver().notifyChange(uri, null);
        }


        return numRowsAffected;
    }
}
