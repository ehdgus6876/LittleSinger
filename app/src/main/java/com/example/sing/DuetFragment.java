package com.example.sing;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class DuetFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private EditText mOutEditText;
    private Button mSendButton;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothChatService mChatService = null;
    private BluetoothAdapter bluetoothAdapter = null; // 블루투스 어댑터

    Intent intent;
    SpeechRecognizer mRecognizer;
    TextView textview;
    final int PERMISSION = 1;
    private int which = 0;
    Context context = null;
    private static final boolean D = true;
    VideoView videoView;
    String out ;
    private int num;
    private int num2;

    private String command;
    private String name;
    String out_lyrics1;
    String out_lyrics2;
    TextView txt;

    public static DuetFragment newInstance(){
        return new DuetFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        FragmentActivity activity = getActivity();
        if (mBluetoothAdapter == null && activity != null) { //activity가 활성화되어있는데 어댑터가 널이면
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }
    }

    @Override
    public void onStart() {

        super.onStart();
        if (mBluetoothAdapter == null) {
            return;
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) { //블루투스 어댑터가  enable이 아닐때
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) { //채팅 서비스가 null이면
            setupChat();
        }
    }

    @Override
    public void onDestroy() {  //프래그먼트가 끝나면
        super.onDestroy();
        if (mChatService != null) { //채팅서비스 null아니면
            mChatService.stop();  //채팅서비스 중지 시키기
        }
    }

    @Override
    public void onResume() { //진행하는 상태
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) { //채팅서비스가 null이 아니면
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) { //채팅서비스 상태가 연결이 안되어있는 상태이면
                // Start the Bluetooth chat services
                mChatService.start();   //채팅 서비스 시작
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,  //채팅이 실행될 떼
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duet, container, false); //채팅 레이아웃을 지금 화면에 뿌림
        textview = view.findViewById(R.id.lyrics);
        txt=view.findViewById(R.id.type);
        context = getActivity();
        // 블루투스 어댑터 객체 설정
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= 23) {
            // 퍼미션 체크
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO}, PERMISSION);
        }



        if (getArguments() != null) {
            which = getArguments().getInt("which");
        }

        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getActivity().getPackageName());

        if (which == 0) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko_KR");
        }
        if (which == 1) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
        }

        videoView = (VideoView) view.findViewById(R.id.video);
        try {
            String video = getArguments().getString("uri");
            name = getArguments().getString("name");
            Uri videofile = Uri.parse(video);
            android.util.Log.d("xxxxx",
                    String.valueOf(video));
            videoView.setVideoURI(videofile);

        } catch (Exception ex) {
            android.util.Log.d(getClass().getName(), "Video failed:" + ex + "");
            ex.printStackTrace();
        }
        ImageButton playbtn = view.findViewById(R.id.playbtn);
        playbtn.setOnClickListener(v -> {
            command="play";
            sendMessage(command);
            num=0;
            num2=0;
            out="";

        });


        ImageButton stopbtn = view.findViewById(R.id.stopbtn);
        stopbtn.setOnClickListener(v -> {
            command="stop";
            sendMessage(command);


        });


        ImageButton pausebtn=view.findViewById(R.id.pausebtn);
        pausebtn.setOnClickListener(v -> {
            command="pause";
            sendMessage(command);
        });
        ImageButton listbtnview = view.findViewById(R.id.listbtn);
        listbtnview.setOnClickListener(v -> {
            command="list";
            sendMessage(command);
        });

        videoView.setOnCompletionListener(mp -> {
            Intent intent= new Intent(getActivity(),MainActivity.class);
            startActivity(intent);
        });


        ImageButton speakbtn=view.findViewById(R.id.speakbtn);
        speakbtn.setOnClickListener(v -> {
            command="rec";
            sendMessage(command);
            mRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());
            mRecognizer.setRecognitionListener(new RecognitionListener() {


                @Override
                public void onReadyForSpeech(Bundle params) {
                    ImageButton speechBtn =view.findViewById(R.id.speakbtn);
                    speechBtn.setImageResource(R.drawable.record);
                    Toast.makeText(getActivity().getApplicationContext(), "음성인식을 시작합니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onBeginningOfSpeech() {
                    ImageButton speechBtn=view.findViewById(R.id.speakbtn);

                    speechBtn.setImageResource(R.drawable.record2);
                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }


                @Override
                public void onBufferReceived(byte[] buffer) {
                }

                @Override
                public void onEndOfSpeech() {
                    ImageButton speechBtn =view.findViewById(R.id.speakbtn);
                    speechBtn.setImageResource(R.drawable.record);
                }

                @Override
                public void onError(int error) {
                    String message;

                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            message = "오디오 에러";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            message = "클라이언트 에러";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            message = "퍼미션 없음";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            message = "네트워크 에러";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            message = "네트웍 타임아웃";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            message = "찾을 수 없음";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            message = "RECOGNIZER가 바쁨";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            message = "서버가 이상함";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            message = "말하는 시간초과";
                            break;
                        default:
                            message = "알 수 없는 오류임";
                            break;
                    }

                    Toast.makeText(getActivity().getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResults(Bundle results) {
                    //말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줍니다.
                    ArrayList<String> matches =
                            results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

                    String recText_org = matches.get(0); //인식된 음성정보
                    String recText = recText_org.replace(" ", ""); //인식된 음성정보 공백제거

                    String lyrics_org = getArguments().getString("lyrics");
                    String lyrics = lyrics_org.replace(" ", ""); //가사정보 공백제거



                    textview.append(recText_org);
                    textview.append(" ");


                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                }


                @Override
                public void onEvent(int eventType, Bundle params) {
                }


            });
            mRecognizer.startListening(intent);

        });

        return view ;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) { //채팅 입력시
        mConversationView = view.findViewById(R.id.in);   //채팅보이는 화면 (대화창)
        mOutEditText = view.findViewById(R.id.edit_text_out); //채팅 입력창
        mSendButton = view.findViewById(R.id.button_send); //전송버튼
    }

    /**
     * Set up the UI and background operations for chat.
     */
    private void setupChat() { //채팅 셋업 하는 메서드
        Log.d(TAG, "setupChat()"); //로그 찍어주자

        // Initialize the array adapter for the conversation thread
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        mConversationArrayAdapter = new ArrayAdapter<>(activity, R.layout.message); //출력된 메세지들 모아놓은 어댑터

        mConversationView.setAdapter(mConversationArrayAdapter); //어댑터를 뷰에 붙임

        // Initialize the compose field with a listener for the return key
        mOutEditText.setOnEditorActionListener(mWriteListener); //작상할시 발생하는 액션 리스너

        // Initialize the send button with a listener that for click events
        mSendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { //전송버튼 클릭시 발생하는 액션 리스너
                // Send a message using content of the edit text widget
                View view = getView();  //뷰를 얻어옴
                if (null != view) { //뷰가 null이 아니면
                    TextView textView = view.findViewById(R.id.edit_text_out); //채팅입력창
                    String message = textView.getText().toString(); //채팅 입력창에 텍스를 스트링 값으로 메세지에 저장
                    sendMessage(message); //메세지 전송
                }
            }
        });

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(activity, mHandler); //블루투스 챗 서비스

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer(); //메세지들 버퍼
    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {  //기기 검색 허용하는 메소드
        if (mBluetoothAdapter.getScanMode() !=                          //내기기가 검색허용모드이면
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE); //허용 인텐트 생성
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); //인텐트에 300초동안 허용 넣어줌
            startActivity(discoverableIntent); //허용요청 화면 띄움
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) { //메세지 보내는 메서드
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) { //챗 서비스 상태가 연결상태가 아니라면
            Toast.makeText(getActivity(), R.string.not_connected, Toast.LENGTH_SHORT).show(); //토스트 메세지 띄움
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {  //메세지 길이가 0보다 크면
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();  //바이트 형식으로 메세지를 배열에 저장
            mChatService.write(send); //쳇서비스에  write(send)

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);  //전송하면 비움
            mOutEditText.setText(mOutStringBuffer); //채팅 입력창도 0으로 바꿈
        }
    }

    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener   //텍스트 뷰에 입력할시 입력 리스너
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {  //action이 더이상 널이거나 버튼에서 손을 때는 액션인경우 즉, 입력이 완료된 경우
                String message = view.getText().toString();  //뷰에 텍스트를 메세지에 저장
                //sendMessage(message); //메세지 전송
            }
            return true;
        }
    };

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) { //상태 설정 메소드
        FragmentActivity activity = getActivity();
        if (null == activity) {   //널이 엑티비티 일때
            return;
        }
        final ActionBar actionBar = activity.getActionBar();  //액티비티의 액션바 상태 actionBar에 불러오기
        if (null == actionBar) { //널이 액션바 일경우
            return;
        }
        actionBar.setSubtitle(resId); //액션 바에 상태 문자 설정
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {  //액션바에 상태설정
        FragmentActivity activity = getActivity();
        if (null == activity) {
            return;
        }
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subTitle); //액션바에 상태설정
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    if(D) android.util.Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;

                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    if(writeMessage.equals("play")){
                        Log.d("play",writeMessage);
                        videoView.start();
                        txt.setText("부를 차례입니다~");
                    }

                    else if(writeMessage.equals("stop")){
                        videoView.stopPlayback();
                        videoView.resume();
                    }
                    else if(writeMessage.equals("pause")){
                        videoView.pause();
                    }
                    else if(writeMessage.equals("list")){
                        ((DuetActivity)getActivity()).replaceFragment(ListFragment.newInstance());
                    }
                    else if(writeMessage.equals("rec")){
                        txt.setText("부를 차례입니다~");
                    }
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    if(readMessage.equals("play")){
                        txt.setText("상대방 차례입니다~");
                        videoView.start();
                    }

                    else if(readMessage.equals("stop")){
                        videoView.stopPlayback();
                        videoView.resume();
                    }
                    else if(readMessage.equals("pause")){
                        videoView.pause();
                    }
                    else if(readMessage.equals("list")){
                        ((DuetActivity)getActivity()).replaceFragment(ListFragment.newInstance());
                    }
                    else if(readMessage.equals("rec")){
                        txt.setText("상대방 차례입니다~");
                    }
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) { //상대 디바이스에서 결과 받아오기
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE: //리퀘스트 연결 secure 상태면
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {  //리퀘스트 코드 가 오케이면
                    connectDevice(data, true);  //얀걀
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE: //리퀘스트 연결 insecure 상태면
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) { //리퀘스트 코드 가 오케이면
                    connectDevice(data, false); //연결 실패
                }
                break;
            case REQUEST_ENABLE_BT: //블루투스 연결 요청 오면
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) { //ok하면
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat(); //셋업 채팅
                } else {//아니면
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled"); //연결 안됨 로그
                    FragmentActivity activity = getActivity();
                    if (activity != null) { //액티비티가 널이아니면
                        Toast.makeText(activity, R.string.bt_not_enabled_leaving,
                                Toast.LENGTH_SHORT).show(); //토스트 띄움
                        activity.finish(); //액티비티 종료
                    }
                }
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) { //디바이스 연결 메소드
        // Get the device MAC address
        Bundle extras = data.getExtras(); //받아온 데이터를 추출
        if (extras == null) { //데이터가 널일시
            return;
        }
        String address = extras.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS); //연결된 디바이스 주소 디바이스 리스트 액티비티에서 추출
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address); //블루투스 어댑터에서 연결된 디바이스 얻어옴
        // Attempt to connect to the device
        mChatService.connect(device, secure); //채팅 서비스에 블루투스 연결된 디바이스로 연결 시도
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {  //블루투스 모양 메뉴 눌렀을시
        inflater.inflate(R.menu.bluetooth_chat, menu); //메뉴바 활성화
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //메뉴바에서 아이템 선택했을때
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: { //secure_connect 일때
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }
            case R.id.insecure_connect_scan: { //insecure_connect 일때
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            }
            case R.id.discoverable: {  //검색허용일때
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
            }
        }
        return false;
    }
}


