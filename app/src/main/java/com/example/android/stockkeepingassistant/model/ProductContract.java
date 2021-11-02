package com.example.android.stockkeepingassistant.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {
	private ProductContract() {
	}

	public static final String CONTENT_AUTHORITY = "com.example.android.stockkeepingassistant";

	private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	public static final String PATH_PRODUCTS = "products";

	public static class ProductEntry implements BaseColumns {
		public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

		public static final String CONTENT_LIST_TYPE =
				ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		public static final String CONTENT_ITEM_TYPE =
				ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

		public static final String TABLE_NAME = "products";
		public static final String _ID = BaseColumns._ID;
		public static final String UUID = "uuid";
		public static final String COLUMN_PRODUCT_TITLE = "title";
		public static final String COLUMN_PRODUCT_PRICE = "price";
		public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
		public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
		public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
	}

}
