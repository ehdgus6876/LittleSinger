package com.example.sing;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class DuetPairing extends Activity {
    private static final String TAG = "DuetPairing";

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private BluetoothAdapter mBluetoothAdapter = null;
    //private BluetoothChatService mChatService = null; //BluetoothChatService클래스 참조

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " );

        setContentView(R.layout.duet_pairing);

        //mChatService = new BluetoothChatService(this,new Handler());
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //객체 참조
        if (mBluetoothAdapter == null){
            Toast.makeText(this,"Bluetooth is not available", Toast.LENGTH_LONG).show(); //블루투스 연결가능하지않음을 toast로 먼저 띄움
            finish();
            return;
        }

        Button scanning = (Button)findViewById(R.id.scanningBtn);
        scanning.setOnClickListener(new View.OnClickListener() { //DeviceList로 이동 (다른기기 찾기)
            @Override
            public void onClick(View v) {
                Intent serverIntent = new Intent(DuetPairing.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        Button scanned = (Button)findViewById(R.id.scannedBtn);
        scanned.setOnClickListener(new View.OnClickListener() { //scanning할 수 있게 허락
            @Override
            public void onClick(View v) {
                ensureDiscoverable();
            }
        });

        Button duetStartBtn = (Button)findViewById(R.id.duetstartBtn);
        duetStartBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, ConnectManagementService.getState()+"  연결 상태 코드");
                if (ConnectManagementService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    Log.d(TAG, ConnectManagementService.getState()+"  연결 상태 코드");
                    Toast.makeText(DuetPairing.this, "블루투스 장치 연결을 해야 대전이 가능합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Log.e(TAG, Info.connect_user_address+"  디바이스++++");
                    Intent duet = new Intent(getApplicationContext(), FightSelectWord.class); //서브액티비티 클래스 전체사용,새로운 객체 받아오기 위한 것
                    startActivity(duet);
                }
            }
        });

        // 블루투스 지원 유무 확인
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 블루투스를 지원하지 않으면 NULL을 리턴
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    private void ensureDiscoverable() {
        Log.e(TAG, "ensureDiscoverable" );

        if (mBluetoothAdapter.getScanMode() != //getScanMode() 메서드를 이용하면 내 블루투스 디바이스가 검색 가능한 상태인지 확인
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); //ACTION_REQUEST_DISCOVERABLE 액션인텐트는 검색 가능한 상태로 만들어주기 위한 시스템 액티비티를 띄우기 위해 정의된 상수
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120); //120초 동안 디바이스가 검색 될 수 있는 상태
            startActivity(discoverableIntent);
        }
    }

    @Override
    //페어링할 새로운 디바이스를 응답받았을 때
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "requestCode: "+requestCode );
        Log.e(TAG, "resultCode: "+resultCode );

        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BluetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device]
                    Info.connect_user_address=address;
                    Info.connect_user_device=device;
                    Info.connect_user_secure=true;
                    ConnectManagementService.startConnection(device,true);

                    //mChatService.connect(device,true);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat(); 여기 고쳐야함!!!!
                    Toast.makeText(this, "듀엣모드 시작 가능", Toast.LENGTH_LONG).show();
                } else {
                    // User did -Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

}

