package com.example.sing;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ScoreMonitorActivity extends AppCompatActivity {
    ListView list;
    ScoreListViewAdapter adapter;
    DB_Open db_open;
    SQLiteDatabase db=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_list);

        list=findViewById(R.id.listing);
        createDB();
        showList();
    }
    protected void showList(){
        adapter=new ScoreListViewAdapter();
        Cursor c=db.rawQuery("SELECT * FROM score",null);
        c.moveToFirst();
        int count=c.getCount();

        for(int i=0;i<count;i++){

            adapter.addItem(c.getString(c.getColumnIndex("name")),c.getString(c.getColumnIndex("score")),c.getString(c.getColumnIndex("date")));
            c.moveToNext();
        }

        list.setAdapter(adapter);


    }
    protected  void createDB(){
        db_open=new DB_Open(this);
        db=db_open.getWritableDatabase();
    }
}
