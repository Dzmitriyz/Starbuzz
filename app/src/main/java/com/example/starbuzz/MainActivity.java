package com.example.starbuzz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    private Cursor favoriteCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupOptionsListView();
        setupFavoritesListView();
        }
        private void setupOptionsListView(){

            AdapterView.OnItemClickListener itemClickListener =new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> listView, View itemView, int position, long id) {
                    if(position == 0){
                        Intent intent = new Intent(MainActivity.this, DrinkCategoryActivity.class);
                        startActivity(intent);
                    }
                }
            };
            ListView listView =(ListView) findViewById(R.id.list_options);
            listView.setOnItemClickListener(itemClickListener);
    }

    private void setupFavoritesListView(){
        ListView listFavorites = findViewById(R.id.list_favorites);
        try {
            SQLiteOpenHelper statbuzzDataHelpter = new StarbuzzDatabaseHelper(this);
            db =statbuzzDataHelpter.getReadableDatabase();
            favoriteCursor = db.query("DRINK",
                    new String[]{"_id","NAME"},
                    "FAVORITE = 1",
                    null,null,null,null);
            CursorAdapter favoriteAdapter = new SimpleCursorAdapter(MainActivity.this,
                    android.R.layout.simple_list_item_1,favoriteCursor,new String[]{"NAME"},new int[]{android.R.id.text1},0);
            listFavorites.setAdapter(favoriteAdapter);
        }catch (SQLiteException e){
            Toast toast = Toast.makeText(this,"Database unavailable", Toast.LENGTH_SHORT);
            toast.show();
        }

        listFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,DrinkActivity.class);
                intent.putExtra(DrinkActivity.EXTRA_DRINKID, (int)id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Cursor newCursor = db.query("DRINK",
                new String[]{"_id","NAME"},
                "FAVORITE = 1",null,null,null,null);
        ListView listFavorite = findViewById(R.id.list_favorites);
        CursorAdapter adapter = (CursorAdapter) listFavorite.getAdapter();
        adapter.changeCursor(newCursor);
        favoriteCursor = newCursor;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        favoriteCursor.close();
        db.close();
    }
}