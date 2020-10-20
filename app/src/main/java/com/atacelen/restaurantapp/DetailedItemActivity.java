package com.atacelen.restaurantapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class DetailedItemActivity extends AppCompatActivity {

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private SQLiteDatabase database;

    ImageView imageView;
    TextView foodNameText, foodPriceText, foodCookingTimeText, foodCategoryText, foodDiscountText;
    String foodName, foodPrice, foodCookingTime, foodCategory, foodDiscount;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        imageView = findViewById(R.id.detailedItem_imageView);
        foodNameText = findViewById(R.id.detailedItem_nameText);
        foodPriceText = findViewById(R.id.detailedItem_priceText);
        foodCookingTimeText = findViewById(R.id.detailedItem_cookingTimeText);
        foodCategoryText = findViewById(R.id.detailedItem_categoryText);
        foodDiscountText = findViewById(R.id.detailedItem_discountText);

        Intent intent = getIntent();

        Singleton singleton = Singleton.getInstance();
        bitmap = singleton.getChosenFoodImage();

        foodName = intent.getStringExtra("detailedFoodName");
        foodPrice = intent.getStringExtra("detailedFoodPrice");
        foodCookingTime = intent.getStringExtra("detailedFoodCookingTime");
        foodCategory = intent.getStringExtra("detailedFoodCategory");
        foodDiscount = intent.getStringExtra("detailedFoodDiscount");

        imageView.setImageBitmap(bitmap);
        foodNameText.setText(foodName);
        foodPriceText.setText("Price: " + foodPrice + " CHF");
        foodCookingTimeText.setText("Cooking Time: " + foodCookingTime + " min");
        foodCategoryText.setText("Category: " + foodCategory);
        foodDiscountText.setText("Discount: " + foodDiscount);
    }

    public void addToFavourites(View view) {
        offlineCache();

        final Uri uri = getImageUriFromBitmap();
        storageReference.child("Images").child(foodName + ".jpg").putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String downloadUrl = uri.toString();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                HashMap<String, Object> foodData = new HashMap<>();

                foodData.put("foodName", foodName);
                foodData.put("foodPrice", foodPrice);
                foodData.put("foodCookingTime", foodCookingTime);
                foodData.put("foodCategory", foodCategory);
                foodData.put("foodDiscount", foodDiscount);
                foodData.put("downloadUrl", downloadUrl);





                firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Favourites").add(foodData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(DetailedItemActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DetailedItemActivity.this, e.getLocalizedMessage(),Toast.LENGTH_LONG);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DetailedItemActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void offlineCache(){

        try {

            database = this.openOrCreateDatabase("FavFoods",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS FavFoods (id INTEGER PRIMARY KEY,foodName VARCHAR, foodPrice VARCHAR, foodCookingTime VARCHAR, " +
                    "foodCategory VARCHAR, foodDiscount VARCHAR, foodImage BLOB)");


            String sqlString = "INSERT INTO FavFoods (foodName, foodPrice, foodCookingTime, foodCategory, foodDiscount, foodImage) VALUES (?, ?, ?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,foodName);
            sqLiteStatement.bindString(2,foodPrice);
            sqLiteStatement.bindString(3,foodCookingTime);
            sqLiteStatement.bindString(4,foodCategory);
            sqLiteStatement.bindString(5,foodDiscount);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] bytes = outputStream.toByteArray();
            sqLiteStatement.bindBlob(6,bytes);
            sqLiteStatement.execute();


        } catch (Exception e) {

        }
    }

    public Uri getImageUriFromBitmap() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }



}