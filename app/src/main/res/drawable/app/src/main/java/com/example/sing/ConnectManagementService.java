package com.example.sing;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class ConnectManagementService extends Service {
    private static final String TAG = "ConnectManageService";


    private static Context mContext;

    // Debugging
//    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    static private BluetoothChatService mChatService = null;
    //private final Handler serviceHandler;

//    private Intent intent;

    public ConnectManagementService(){

    }
    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: " );
        mChatService = new BluetoothChatService(this, serviceHandler);
        mChatService.start(); //제어할 BluethoothChatService
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
        mContext = this;

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행
        Log.d("test", "서비스 도착");
        //startConnection(Info.connect_user_device, Info.connect_user_secure);

        Log.d("test", Info.connect_user_device+"장치연결중");
        Log.d("test", "연결 시도");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
        Log.d("test", "서비스의 onDestroy");
    }

    static public void startConnection(BluetoothDevice device, Boolean secure){ //블루투스 연결을 실행하는 함수
        mChatService.connect(device,secure);
    }
    public void startDisconnection(){

    }
    static public void sendWord(String word){ //단어보냄

        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(mContext, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (word.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] sendByte = word.getBytes();
            mChatService.write(sendByte);

            //FightWait.isSelected=true;
            Log.d("test", "데이터 보냄(서비스)");
        }
    }

    static int getState(){
        return mChatService.getState();
    }

    private final Handler serviceHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //mContent.setText(R.string.title_connected_to);
                            //mContent.append(mConnectedDeviceName);
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            // mContent.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage); //화면에 보여지는 부분
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
//                    Intent intent = new Intent("org.techtown.please.DataReceive");
                    Intent intent = new Intent("org.techtown.please.DataReceive");
                    if(DuetWait.isSelected) { //fightselectword.java 에서 수신
//                        Intent intent = new Intent("org.techtown.please.DataReceive");
                        Log.e(TAG, "handleMessage: DuetWait.첫번째" + DuetWait.isSelected );
                        intent.putExtra("data", readMessage);
                        Log.e(TAG, "handleMessage: "+readMessage );
                        sendBroadcast(intent); //송신측, BroadcastReceiver에 수신됨

                    } else {
                        Log.e(TAG, "handleMessage: DuetWait.두번째: " +DuetWait.isSelected);
                        DuetWait.isSelected=true;
                        DuetWait.word = readMessage;
                    }
                    if(FightFinish.isFinishData) {
                        intent.putExtra("finishData", readMessage);
                        sendBroadcast(intent);
                    } else {
                        FightFinish.isFinishData=true;
                        FightFinish.getdata = readMessage;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    Info.connect_user_address = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + Info.connect_user_address, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

}

