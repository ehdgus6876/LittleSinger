package com.example.sing;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class FightSelectWord extends Activity {
    private static final String TAG = "FightSelectWord";

    // var
    private ArrayList<String> items = new ArrayList<>(); // 단어리스트 데이터 변수

    // component
    private TextView text; // TextView 형태를 선언.
    private ImageButton sendDataBtn;
    private ImageButton resetDataBtn;
    private Toolbar toolbar;
    private ListView listView;

    //others
    private ArrayAdapter adapter;  // 리스트뷰 어댑터
    private BluetoothAdapter bluetoothAdapter = null; // 블루투스 어댑터
    private BluetoothChatService bluetoothChatService = null; // BluetoothChatService 객체

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fight_select_word);
        Log.e(TAG, "onCreate: ");

        // 컴포넌트 연결
        text = (TextView) findViewById(R.id.tv);
        toolbar = (Toolbar) findViewById(R.id.fight_select_toolbar);
        listView = (ListView) findViewById(R.id.fight_select_listview);
        sendDataBtn = (ImageButton) findViewById(R.id.sendDataBtn);
        resetDataBtn = (ImageButton) findViewById(R.id.resetDataBtn);

        // 입력한 데이터 적용
        setItems();

        // 리스트뷰 어댑터 설정
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,items);
        listView.setAdapter(adapter);

        // 블루투스 어댑터 객체 설정
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(FightSelectWord.this, items.get(i), Toast.LENGTH_SHORT).show();
            }
        });
        text.setText("듀엣모드에 동의하시겠습니까 ? ");
        toolbar.setTitle("SELECTING WORD");
    }

    /**
     * 데이터 입력 함수
     */
    private void setItems() {

        items.add("convince");
        items.add("order");
        items.add("create");
        items.add("restore");
        items.add("urgency");
        items.add("revenue");
        items.add("intense");
        items.add("collapse");
    }
    /**
     * 버튼별 클릭
     *
     * @param v
     */
    public void onClick(View v) {
        switch (v.getId()) {

            // FIGHT 버튼
            case R.id.sendDataBtn:
                SparseBooleanArray booleanArray = listView.getCheckedItemPositions();

                // 보낼 데이터
                ArrayList<String> arrayList = new ArrayList<>();

                // 체크된 리스트 데이터만 삽입
                for(int i =0; i<items.size();i++){
                    if(booleanArray.get(i)){
                        arrayList.add(items.get(i));
                    }
                }

                String sendToString = "accept";
                ConnectManagementService.sendWord(sendToString);
                Intent duetwait = new Intent(getApplicationContext(), DuetWait.class); //서브액티비티 클래스 전체사용,새로운 객체 받아오기 위한 것
                duetwait.putStringArrayListExtra("words",arrayList);
                startActivity(duetwait);
                break;

            // RESET 버튼
            case R.id.resetDataBtn:
                // 모든 체크 해제
                listView.clearChoices();
                adapter.notifyDataSetChanged();

                Toast.makeText(this, "단어선택이 초기화", Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
