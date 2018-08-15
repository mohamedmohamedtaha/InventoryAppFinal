package com.imagine.mohamedtaha.store;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.mohamedtaha.store.data.TaskContract;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;


public class TestFragmentContentProvider extends DialogFragment {
    private static final String EXTRA_ID = "id";
    private static final int PICK_FROM_GALLERY = 1;
    EditText et_product_name, et_product_price, et_product_quantity, et_supplier_name, et_supplier_phone;
    ImageView product_picture;
    Uri uri;
    AlertDialog.Builder builder;
    AlertDialog dialog;
    long id;
    private TextView TVTitleCategory;
    private Button BTAddCategory, bt_galary;

    public static TestFragmentContentProvider newInstance(long id) {
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_ID, id);

        TestFragmentContentProvider testFragmentContentProvider = new TestFragmentContentProvider();
        testFragmentContentProvider.setArguments(bundle);
        return testFragmentContentProvider;
    }

    //method for convert picture
    public static byte[] imageViewToByte(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        return bytes;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PICK_FROM_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            } else {
                Toast.makeText(getActivity(), "You don't have permission to access ", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                product_picture.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_content_provider, null);
        TVTitleCategory = (TextView) view.findViewById(R.id.TVTitleCategory);
        et_product_name = (EditText) view.findViewById(R.id.et_product_name);
        et_product_price = (EditText) view.findViewById(R.id.et_product_price);
        et_product_quantity = (EditText) view.findViewById(R.id.et_quantity);
        et_supplier_name = (EditText) view.findViewById(R.id.et_supplier_name);
        et_supplier_phone = (EditText) view.findViewById(R.id.et_supplier_phone);
        BTAddCategory = (Button) view.findViewById(R.id.BTAddCategory);
        product_picture = (ImageView) view.findViewById(R.id.product_picture);
        bt_galary = (Button) view.findViewById(R.id.bt_galary);
        bt_galary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                    } else {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (getArguments() != null && getArguments().getLong(EXTRA_ID) != 0) {
            id = getArguments().getLong(EXTRA_ID);
            BTAddCategory.setText(getString(R.string.bt_edit));
            TVTitleCategory.setText(getString(R.string.edit_category_titile));
            uri = Uri.withAppendedPath(TaskContract.TaskEntry.CONTENT_URI, String.valueOf(id));
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToNext()) {
                et_product_name.setText(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.KEY_PRODUCT_NAME)));
                et_product_price.setText(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.KEY_PRICE)));
                et_product_quantity.setText(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.KEY_QUANTITY)));
                et_supplier_name.setText(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.KEY_SUPPLIER_NAME)));
                et_supplier_phone.setText(cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.KEY_SUPPLIER_PHONE_NUMBER)));
                byte[] product_picture_index = cursor.getBlob(cursor.getColumnIndex(TaskContract.TaskEntry.KEY_PICTURE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(product_picture_index, 0, product_picture_index.length);
                product_picture.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));


            }
            cursor.close();
        }
        //__________________________________This with onClick Method_____________________
        BTAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCategory();
            }
        });

        builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setIcon(R.mipmap.ic_reports);
        dialog = builder.create();
        dialog.show();
        return dialog;
    }

    private void saveCategory() {
        //Read from input field,use trim to eliminate leading or trailing wgite spase
        String product_name_string = et_product_name.getText().toString().trim();
        String product_price_string = et_product_price.getText().toString().trim();
        String quantity_string = et_product_quantity.getText().toString().trim();
        String supplier_name_string = et_supplier_name.getText().toString().trim();
        String supplier_phone_string = et_supplier_phone.getText().toString().trim();

        if (product_picture.getDrawable() == null) {
            Toast.makeText(getActivity(), getString(R.string.error_image), Toast.LENGTH_LONG).show();
            return;
        }

        if (getArguments() == null && TextUtils.isEmpty(product_name_string)) {
            Toast.makeText(getActivity(), getString(R.string.error_empty_text), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(product_price_string) || Integer.parseInt(product_price_string) <= 0) {
            Toast.makeText(getActivity(), getString(R.string.error_empty_text_or_zero), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(quantity_string) || Integer.parseInt(quantity_string) <= 0) {
            Toast.makeText(getActivity(), getString(R.string.error_empty_text_or_zero), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(supplier_name_string)) {
            Toast.makeText(getActivity(), getString(R.string.error_empty_text_name), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(supplier_phone_string)) {
            Toast.makeText(getActivity(), getString(R.string.error_empty_phone), Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.KEY_PRODUCT_NAME, product_name_string);
        values.put(TaskContract.TaskEntry.KEY_PRICE, product_price_string);
        values.put(TaskContract.TaskEntry.KEY_QUANTITY, quantity_string);
        values.put(TaskContract.TaskEntry.KEY_SUPPLIER_NAME, supplier_name_string);
        values.put(TaskContract.TaskEntry.KEY_SUPPLIER_PHONE_NUMBER, supplier_phone_string);
        values.put(TaskContract.TaskEntry.KEY_PICTURE, imageViewToByte(product_picture));

        //Determine if this is a new or existing Category bychecking if mCurrentCategoryURi is null or not
        if (getArguments() == null) {
            //This is a new Category , so insert a new Category into the provider,
            //returning the content URI for the new category
            Uri newUri = getContext().getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, values);
            //Show a Toast message depending on whether or not the inserting was successful.
            if (newUri == null) {
                //If the new content URI is null,then there was an error with inserting
                Toast.makeText(getContext(), getString(R.string.editor_insert_category_failed), Toast.LENGTH_LONG).show();
            } else {
                //Otherwise ,the inserting was successful and we can display a toast
                Toast.makeText(getContext(), getString(R.string.editor_insert_category_successful), Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
        } else {
            //Otherwise this is an Existing Category , soupdate the category with content URI: mCurrentCategoryUri
            //and pass in the new ContentValues. pasin null for the selection and selection args
            //becausa mCurrentCategoryUri will already identify the correct row in the database that we want to modify.
            uri = Uri.withAppendedPath(TaskContract.TaskEntry.CONTENT_URI, String.valueOf(id));

            int rowsAffected = getContext().getContentResolver().update(uri, values, null, null);
            //Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                //If no rows were affected,then there was an error with the update
                //   Toast.makeText(this, getString(R.string.editor_update_category_failed), Toast.LENGTH_SHORT).show();
                Toast.makeText(getContext(), getString(R.string.editor_update_category_failed), Toast.LENGTH_SHORT).show();

            } else {
                //Otherwise, the update was successful and we can display a toast.
                Toast.makeText(getContext(), getString(R.string.editor_update_category_successful), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        }
    }
}


