package com.imagine.mohamedtaha.store;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;


import com.imagine.mohamedtaha.store.adapter.AdapterAddCategoryContentProvider;

import com.imagine.mohamedtaha.store.data.TaskContract.TaskEntry;


public class Add_Category_Fragment_ContentProvider extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //Identifier for the category dataloader;
    private static final int CATEGORY_LOADER = 0;
    // TODO: Rename parameter arguments, choose names that match
    FloatingActionButton fabAddCategory;
    RecyclerView mRecycleView;
    AdapterAddCategoryContentProvider adapterAddCategoryContentProvider;
    View emptView;
    int first, finalResult;
    Uri uri;
    private LinearLayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add__category_);
        mRecycleView = (RecyclerView) findViewById(R.id.recycleViewAddCategory);

        mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);

        emptView = findViewById(R.id.empty_view_category);

        adapterAddCategoryContentProvider = new AdapterAddCategoryContentProvider(new AdapterAddCategoryContentProvider.showDetial() {
            @Override
            public void itemShowDetail(Cursor cursor) {
                Intent intent = new Intent(Add_Category_Fragment_ContentProvider.this, DetailActivity.class);
                long id = cursor.getLong(cursor.getColumnIndex(TaskEntry._ID));
                intent.putExtra("id", id);
                startActivity(intent);
                finish();


            }
        }, this, new AdapterAddCategoryContentProvider.showSale() {
            @Override
            public void itemShowSale(Cursor cursor) {
                long id = cursor.getLong(cursor.getColumnIndex(TaskEntry._ID));
                uri = Uri.withAppendedPath(TaskEntry.CONTENT_URI, String.valueOf(id));
                first = deleteQuantity();
                finalResult = first - 1;
                if (finalResult < 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_quantity_lees_zero), Toast.LENGTH_SHORT).show();
                    return;

                }

                ContentValues values = new ContentValues();
                values.put(TaskEntry.KEY_QUANTITY, finalResult);
                uri = Uri.withAppendedPath(TaskEntry.CONTENT_URI, String.valueOf(id));
                int rowsAffected = getContentResolver().update(uri, values, null, null);
                //Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    //If no rows were affected,then there was an error with the update
                    Toast.makeText(getApplicationContext(), getString(R.string.editor_update_category_failed), Toast.LENGTH_SHORT).show();

                } else {
                    //Otherwise, the update was successful and we can display a toast.
                }
            }
        }, new AdapterAddCategoryContentProvider.showEdit() {
            @Override
            public void itemShowEdit(Cursor cursor) {
                long id = cursor.getLong(cursor.getColumnIndex(TaskEntry._ID));
                TestFragmentContentProvider f = TestFragmentContentProvider.newInstance(id);
                f.show(getSupportFragmentManager(), "dialog");
            }
        });


        fabAddCategory = (FloatingActionButton) findViewById(R.id.fab_add_category);
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TestFragmentContentProvider().show(getSupportFragmentManager(), "dialog");


            }
        });
        mRecycleView.setAdapter(adapterAddCategoryContentProvider);

        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);

    }

    public int deleteQuantity() {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        int sumQuantity = 0;
        cursor.moveToFirst();
        if (cursor != null && cursor.getCount() > 0) {
            sumQuantity = cursor.getInt(cursor.getColumnIndex(TaskEntry.KEY_QUANTITY));
        }
        cursor.close();
        return sumQuantity;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {TaskEntry._ID, TaskEntry.KEY_PRODUCT_NAME, TaskEntry.KEY_PRICE, TaskEntry.KEY_QUANTITY
                , TaskEntry.KEY_PICTURE};
        return new CursorLoader(this,
                TaskEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);                // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0) {
            mRecycleView.setVisibility(View.VISIBLE);
            emptView.setVisibility(View.GONE);
            adapterAddCategoryContentProvider.swapCursor(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapterAddCategoryContentProvider.swapCursor(null);

    }
}