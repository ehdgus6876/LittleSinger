package com.example.sing;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Set;

public class DeviceListActivity extends Activity {
    // Debugging
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;

    // Return Intent extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    // Member fields
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);
        Log.e(TAG, "onCreate: " );

        // Set result CANCELED incase the user backs out
        setResult(Activity.RESULT_CANCELED);

        // 버튼을 눌렀을 때 검색 시작 (doDiscovery)
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() { //주변 블루투스 디바이스 찾는 버튼
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onStart() {
        Log.e(TAG, "+++디바이스 초기화 +++");
        // Initialize array adapters. One for already paired devices and
        // one for newly discovered devices
        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // Find and set up the ListView for paired devices
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDeviceClickListener);

        // 새로 찾아진 디바이스 정보를 보여주기 위한 어댑터 객체
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // 블루투스 디바이스가 찾아졌을 때 인텐트를 전달받기 위한 인텐트 필터 등록
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // 검색 과정이 끝났을 때 인텐트를 전달받기 위한 인텐트 필터 등록
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.d("test",device.getName().toString() + "Device is connected!!");
            }
        } else { //페어링했던 디바이스가 없을 때
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
            Log.e(TAG, "+++페어링한 디바이스 없음 +++");
        }

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }

    /**
     * Start device discover with the BluetoothAdapter
     */
    private void doDiscovery() {
        if (D) Log.d(TAG, "doDiscovery()");
        Log.e(TAG, "+++do Discovery +++");

        // Indicate scanning in the title
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // Turn on sub-title for new devices
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // 접근 승낙 상태 일때
            Log.e(TAG, "+++접근승낙 +++");
        }
        else{
            // 접근 거절 상태 일때
            Log.e(TAG, "+++접근거절 +++");
            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            //int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            //  ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CALENDAR},0);
        }
        // 블루투스 검색 시작
        mBtAdapter.startDiscovery();
        Log.e(TAG, "+++새로운 디바이스 검색 +++");
    }

    // 디바이스가 검색되었을 때 그 중 하나를 터치했을 경우 연결되는 리스너
    // 리스트뷰의 한 아이템을 터치하였을 때 메인 액티비티로 돌아가는 코드
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            mBtAdapter.cancelDiscovery(); // 검색 중지

            // 선택된 아이템에서 MAC주소 확인
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // 인텐트 객체에 MAC주소 추가
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // 메인 액티비티로 응답
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    //인텐트 필터를 등록할 대 파라미터로 전달된 브로드캐스트 리시버는 다음과 같음
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("result","device action!!");
            Log.e(TAG, "+++ device action! +++");

            if (BluetoothDevice.ACTION_FOUND.equals(action)) { // 블루투스 디바이스가 검색되었을 때
                // 인텐트로 전달된 BluetoothDevice 객체 참조
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                Log.e(TAG,"device getName : "+device.getName());

                //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                Log.e(TAG, "+++검색 됨 +++");
                // 찾아진 디바이스가 페어링되어 있지 않을 경우
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
//                    Log.d(TAG,device.getName().toString() + "Device find!!");
                }
                // 블루투스 검색이 끝났을 때
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.e(TAG, "+++검색 끝 +++");
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) { //페어링할 디바이스가 없을 때
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                    Log.e(TAG, "+++새로운 디바이스 못찾음 +++");
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

                Log.e(TAG,"device discovery start");
            }
        }
    };
}
