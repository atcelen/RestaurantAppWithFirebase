package com.atacelen.restaurantapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class FavouritesActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    ArrayList<String> foodNameFromFB, foodPriceFromFB, foodCookingTimeFromFB, foodCategoryFromFB, foodDiscountFromFB;
    ArrayList<Bitmap> foodImagesFromFB;
    MenuRecyclerAdapter menuRecyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFav);

        foodNameFromFB = new ArrayList<>();
        foodPriceFromFB = new ArrayList<>();
        foodCookingTimeFromFB = new ArrayList<>();
        foodCategoryFromFB = new ArrayList<>();
        foodDiscountFromFB = new ArrayList<>();
        foodImagesFromFB = new ArrayList<>();



        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerAdapter = new MenuRecyclerAdapter(foodNameFromFB, foodPriceFromFB, foodCookingTimeFromFB, foodCategoryFromFB, foodDiscountFromFB, foodImagesFromFB);
        recyclerView.setAdapter(menuRecyclerAdapter);

        if(isNetworkAvailable()){
            getDataFromFirestore();
            System.out.println("from Firestore...");
        } else {
            getDataFromSQLDatabase();
            System.out.println("from SQL Database...");
        }






    }

    public void getDataFromFirestore() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        System.out.println(firebaseUser.getEmail());
        CollectionReference collectionReference = firebaseFirestore.collection("Users").document(firebaseUser.getEmail()).collection("Favourites");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(FavouritesActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        String foodName = (String) data.get("foodName");
                        String foodPrice = (String) data.get("foodPrice");
                        String foodCookingTime = (String) data.get("foodCookingTime");
                        String foodCategory = (String) data.get("foodCategory");
                        String foodDiscount = (String) data.get("foodDiscount");
                        String url = (String) data.get("downloadUrl");

                        foodNameFromFB.add(foodName);
                        foodPriceFromFB.add(foodPrice);
                        foodCookingTimeFromFB.add(foodCookingTime);
                        foodCategoryFromFB.add(foodCategory);
                        foodDiscountFromFB.add(foodDiscount);

                        Target target = new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                foodImagesFromFB.add(bitmap);
                                menuRecyclerAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        };
                        Picasso.get().load(url).into(target);


                    }
                }
            }
        });
    }




    public void getDataFromSQLDatabase(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("FavFoods", MODE_PRIVATE, null);

            Cursor cursor = database.rawQuery("SELECT * FROM FavFoods", null);
            int nameIx = cursor.getColumnIndex("foodName");
            int priceIx = cursor.getColumnIndex("foodPrice");
            int cookingTimeIx = cursor.getColumnIndex("foodCookingTime");
            int categoryIx = cursor.getColumnIndex("foodCategory");
            int discountIx = cursor.getColumnIndex("foodDiscount");
            int imageIx = cursor.getColumnIndex("foodImage");

            while (cursor.moveToNext()) {
                foodNameFromFB.add(cursor.getString(nameIx));
                foodPriceFromFB.add(cursor.getString(priceIx));
                foodCookingTimeFromFB.add(cursor.getString(cookingTimeIx));
                foodCategoryFromFB.add(cursor.getString(categoryIx));
                foodDiscountFromFB.add(cursor.getString(discountIx));
                Bitmap bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(imageIx), 0, cursor.getBlob(imageIx).length);
                foodImagesFromFB.add(bitmap);
                menuRecyclerAdapter.notifyDataSetChanged();
            }

            cursor.close();

        } catch (Exception e) {
            System.out.println("catch");
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


}