package com.atacelen.restaurantapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class AddItemActivity extends AppCompatActivity {

    Bitmap selectedImage;
    ImageView imageView;
    TextView foodName, foodPrice, foodCookingTime, foodCategory, foodDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        imageView = findViewById(R.id.imageView);
        foodName = findViewById(R.id.foodName);
        foodPrice = findViewById(R.id.foodPrice);
        foodCookingTime = findViewById(R.id.foodCookingTime);
        foodCategory = findViewById(R.id.foodCategory);
        foodDiscount = findViewById(R.id.foodDiscount);
    }

    public void add(View view) {
        Intent intentToMainActivity = new Intent (AddItemActivity.this, MainActivity.class);
        intentToMainActivity.putExtra("intentID", "intent_from_AddItemActivity");
        intentToMainActivity.putExtra("addedFoodName", foodName.getText().toString());
        intentToMainActivity.putExtra("addedFoodPrice", foodPrice.getText().toString());
        intentToMainActivity.putExtra("addedFoodCookingTime", foodCookingTime.getText().toString());
        intentToMainActivity.putExtra("addedFoodCategory", foodCategory.getText().toString());
        intentToMainActivity.putExtra("addedFoodDiscount", foodDiscount.getText().toString());

        Singleton singleton = Singleton.getInstance();
        singleton.setChosenFoodImage(((BitmapDrawable) imageView.getDrawable()).getBitmap());

        intentToMainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentToMainActivity);


    }

    public void selectImage(View view) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery, 2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery, 2);
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            try {
                if(Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), imageData);
                    selectedImage = ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                } else {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}