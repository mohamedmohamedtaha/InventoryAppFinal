package com.imagine.mohamedtaha.store.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by MANASATT on 25/11/17.
 */

public class TaskContract {
    /*
      Clients need to know how to access the task data, and it's your job to provide
         these content URI's for the path to that data:
            1) Content authority,
            2) Base content URI,
            3) Path(s) to the tasks directory
            4) Content URI for data in the TaskEntry class

     */
    //The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.imagine.mohamedtaha.store";

    //The base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //Define the possible paths for accessing data in this contract
    //This is the path for the "tasks" directory
    public static final String PATH_TASKS = "category";


    /* TaskEntry is an inner class that defines the contents of the task table */
    public static final class TaskEntry implements BaseColumns {
        //TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        //Table Names
        public static final String TABLE_CATEGORIES = "categories";

        // Since TaskEntry implements the interfaces "BaseColumns", it has an automatically produced
        // "_ID" column in addition to the two below


        public static final String KEY_PRODUCT_NAME = "product_name";
        public static final String KEY_PRICE = "price";
        public static final String KEY_QUANTITY = "quantity";
        public static final String KEY_SUPPLIER_NAME = "supplier_name";
        public static final String KEY_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
        public static final String KEY_PICTURE = "picture";

    }


}
























