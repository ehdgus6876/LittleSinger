package com.example.sing;

        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.content.ContextCompat;
        import androidx.fragment.app.FragmentTransaction;

        import android.app.Activity;
        import android.content.Intent;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.Button;
        import android.widget.ListView;

public class ListActivity extends Activity implements View.OnClickListener {
    ListView list;
    ListViewAdapter adapter;
    DB_Open db_open;
    SQLiteDatabase db=null;
    private int division=0;
    private int which=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);



        list = findViewById(R.id.listing);
        createDB();
        showList();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c;
                position=position+1;
                //Log.d("xxx", String.valueOf(position));
                Intent div=getIntent();

                division=div.getExtras().getInt("division");

                if(division==1){
                    Intent intent = new Intent(getApplicationContext(),PracticeActivity.class);
                    c=db.rawQuery("SELECT * FROM sing4 WHERE id="+position,null);
                    c.moveToFirst();
                    String uri = c.getString(c.getColumnIndex("uri"));
                    String lyrics=c.getString(c.getColumnIndex("lyrics"));
                    String name=c.getString(c.getColumnIndex("name"));
                    intent.putExtra("lyrics",lyrics);
                    intent.putExtra("name",name);
                    intent.putExtra("uri",uri);
                    intent.putExtra("which",which);
                    startActivity(intent);
                }
                else if(division==2){
                    Intent intent2=new Intent(getApplicationContext(),RecordingActivity2.class);
                    c=db.rawQuery("SELECT * FROM sing4 WHERE id="+position,null);
                    c.moveToFirst();
                    String uri = c.getString(c.getColumnIndex("uri"));
                    intent2.putExtra("uri",uri);
                    intent2.putExtra("which",which);
                    startActivity(intent2);
                }
            }
        });
    }
    protected  void createDB(){
        db_open=new DB_Open(this);
        db=db_open.getWritableDatabase();
    }
    protected void showList(){
        adapter=new ListViewAdapter();
        // 첫 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.a),
                "곰 세마리") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.b),
                "엄마돼지 아기돼지") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.c),
                "여름 냇가") ;
        // 네 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.d),
                "코끼리 아저씨") ;
        // 다섯번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.e),
                "작은 동물원") ;
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.f),
                "개구리") ;
        /*adapter.addItem(ContextCompat.getDrawable(this, R.drawable.g),
                "나무를 심자") ;
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.h),
                "내가 먼저 가야해요") ;
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.i),
                "악어 떼") ;
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.j),
                "올챙이와 개구리") ;*/


        list.setAdapter(adapter);

    }


    @Override
    public void onClick(View v) {

    }
}


