package com.imagine.mohamedtaha.store;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.mohamedtaha.store.data.TaskContract.TaskEntry;

public class DetailActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 1;
    ImageView product_picture;
    Uri uri;
    String show_phone;
    long id;
    Cursor cursor;
    int first;
    int finalResult;
    private TextView tv_product_name, tv_product_price, tv_product_quantity, tv_supplier_name, tv_supplier_phone;
    private Button bt_order, bt_dec, bt_indec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);
        Bundle intent = getIntent().getExtras();

        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_product_price = (TextView) findViewById(R.id.tv_product_price);
        tv_product_quantity = (TextView) findViewById(R.id.tv_product_quantity);
        tv_supplier_name = (TextView) findViewById(R.id.tv_supplier_name);
        tv_supplier_phone = (TextView) findViewById(R.id.tv_supplier_phone);
        product_picture = (ImageView) findViewById(R.id.product_picture_detail);

        bt_order = (Button) findViewById(R.id.bt_order);
        bt_dec = (Button) findViewById(R.id.bt_dec);
        bt_indec = (Button) findViewById(R.id.bt_incre);

        bt_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first = deleteQuantity();
                finalResult = first + 1;
                tv_product_quantity.setText(finalResult + "");
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
        });

        bt_indec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first = deleteQuantity();
                finalResult = first - 1;
                if (finalResult < 0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_quantity_lees_zero), Toast.LENGTH_SHORT).show();
                    return;

                }

                tv_product_quantity.setText(finalResult + "");
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
        });

        bt_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                try {
                    if (ActivityCompat.checkSelfPermission(DetailActivity.this,
                            Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE},
                                REQUEST_PHONE_CALL);

                    } else {
                        callIntent.setData(Uri.parse("tel:" + show_phone));

                        startActivity(callIntent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        if (intent != null) {
            id = intent.getLong("id");

            uri = Uri.withAppendedPath(TaskEntry.CONTENT_URI, String.valueOf(id));
            cursor = this.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToNext()) {
                tv_product_name.setText(cursor.getString(cursor.getColumnIndex(TaskEntry.KEY_PRODUCT_NAME)));
                tv_product_price.setText(cursor.getString(cursor.getColumnIndex(TaskEntry.KEY_PRICE)));
                tv_product_quantity.setText(cursor.getString(cursor.getColumnIndex(TaskEntry.KEY_QUANTITY)));
                tv_supplier_name.setText(cursor.getString(cursor.getColumnIndex(TaskEntry.KEY_SUPPLIER_NAME)));
                tv_supplier_phone.setText(cursor.getString(cursor.getColumnIndex(TaskEntry.KEY_SUPPLIER_PHONE_NUMBER)));
                //for convert Image
                byte[] product_picture_index = cursor.getBlob(cursor.getColumnIndex(TaskEntry.KEY_PICTURE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(product_picture_index, 0, product_picture_index.length);
                product_picture.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
                show_phone = cursor.getString(cursor.getColumnIndex(TaskEntry.KEY_SUPPLIER_PHONE_NUMBER));

            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int items = item.getItemId();
        switch (items) {
            case android.R.id.home:
                Intent s = new Intent(DetailActivity.this, Add_Category_Fragment_ContentProvider.class);
                startActivity(s);
                finish();
                break;
            case R.id.m_delete:
                showDeleteConfirmationDialog();
                break;
            default:

        }
        return super.onOptionsItemSelected(item);
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

    private void showDeleteConfirmationDialog() {
        //Create an AlertDialog.Builder and set the message,and click listeners
        //for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //User clicked the "Delete" button,so delete the Category
                deleteCategory();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //USer clciked the "cancel" button ,so dismiss the dialog and continue editing the category
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        //Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Performthe deletion of the category in the database
    private void deleteCategory() {
        //Only perform the delete if this is an exisitng category
        if (id != 0) {

            int rowsDeleted = this.getContentResolver().delete(uri, null, null);
            //Show a toast mesage depending on whether ornot teh delete was successful.
            if (rowsDeleted == 0) {
                //If no rows were deleted,then there was an error with the delete.
                Toast.makeText(getApplicationContext(), getString(R.string.editor_delete_Category_failed), Toast.LENGTH_SHORT).show();


            } else {
                //Otherwise,the delete was successful and we can display a toast.
                Toast.makeText(getApplicationContext(), getString(R.string.editor_delete_category_successful), Toast.LENGTH_SHORT).show();
                finish();
                Intent s = new Intent(DetailActivity.this, Add_Category_Fragment_ContentProvider.class);
                startActivity(s);
            }

        } else {
            Toast.makeText(getApplicationContext(), "Error : ", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent s = new Intent(DetailActivity.this, Add_Category_Fragment_ContentProvider.class);
        startActivity(s);
        finish();
    }
}
