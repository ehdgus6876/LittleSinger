package com.example.sing;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.icu.text.SimpleDateFormat;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class RecordingActivity2 extends AppCompatActivity implements SurfaceHolder.Callback{

    private Camera camera;
    private MediaRecorder mediaRecorder;
    private ImageButton btn_start, btn_stop;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    String filename;
    private boolean recording = false;
    private int currentCameraId;
    VideoView  videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording2);

        videoView=(VideoView)findViewById(R.id.video2);
        try{
            Intent intent = getIntent();
            String video = intent.getExtras().getString("uri");
            Uri videofile=Uri.parse(video);
            videoView.setVideoURI(videofile);
        }catch(Exception ex){
            Log.d(getClass().getName(),"Video failed:"+ex+"");
            ex.printStackTrace();
        }
        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("녹화를 위하여 권한을 허용해주세요.")
                .setDeniedMessage("권한이 거부되었습니다. 설정 > 권한에서 허용해주세요.")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO)
                .check();
        String sdcard= Environment.getExternalStorageDirectory().getAbsolutePath();
        filename=sdcard+File.separator+"recorded.mp4";
        btn_start = (ImageButton)findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recording) { //레코딩 중이면
                    recordFinish();
                } else { //처음으로 레코딩 중이면
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecordingActivity2.this, "녹화가 시작되었습니다.", Toast.LENGTH_SHORT).show();
                            try {
                                mediaRecorder = new MediaRecorder();
                                camera.unlock(); //카메라는 최초에 잠겨있음
                                mediaRecorder.setCamera(camera); //카메라등록
                                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER); //버튼눌렀을때 캠코더시작된다는 소리가 나게 해줌
                                mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); //비디오source를 카메라에 넣어준다
                                mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P)); //동영상 녹화 화질을 좋게 해준다.
                                mediaRecorder.setOrientationHint(270); //촬영 각도 맞추기
                                mediaRecorder.setOutputFile(filename); //저장경로
                                mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface()); //실제로 보이는 미리보기 화면
                                mediaRecorder.prepare(); //준비
                                videoView.start();
                                mediaRecorder.start(); //시작
                                recording = true; //레코딩 진행중
                            } catch (Exception e) {
                                e.printStackTrace();
                                mediaRecorder.release(); //촬영 종료
                            }
                        }
                    });
                }
            }
        });
        btn_stop=(ImageButton)findViewById(R.id.btn_stop);
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordFinish();
            }
        });
        videoView.setOnCompletionListener(mp -> {
            recordFinish();
            ContentValues values = new ContentValues(10);

            values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Video");
            values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Audio.Media.DATA, filename);

            Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            if (videoUri == null) {
                Log.d("recorder", "Video insert failed.");
                return;
            }

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));

        });
    }

    PermissionListener permission = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(RecordingActivity2.this, "권한 허가", Toast.LENGTH_SHORT).show();


            if(currentCameraId==Camera.CameraInfo.CAMERA_FACING_BACK) {
                currentCameraId=Camera.CameraInfo.CAMERA_FACING_FRONT;
            }
            camera=Camera.open(currentCameraId);
            camera.setDisplayOrientation(90);
            surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(RecordingActivity2.this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(RecordingActivity2.this, "권한 거부", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void surfaceCreated(SurfaceHolder holder) { //처음 생성되었을때

    }

    private void refreshCamera(Camera camera) {
        if (surfaceHolder.getSurface() == null) {
            return;
        }
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCamera(camera);
    }

    private void setCamera(Camera cam) {
        camera = cam;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { //surfaceview에 변화가 일어나고 있을 때 감지한다.
        refreshCamera(camera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void recordFinish(){
        videoView.stopPlayback();
        videoView.resume();
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        camera.lock();
        recording = false;
    }

}
