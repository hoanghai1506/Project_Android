package com.example.soundrecorder;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Recording extends Fragment  implements View.OnClickListener  {
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton record_btn;
    private ImageButton stop_btn;
    private TextView time;
    private boolean run=false;
    private Runnable r;
    private boolean isRecording= true;
    private  int PERMISSION_CODE = 21;
    private ImageButton cancel,stop;
    private  String checkPermission = "Manifest.permission.RECORD_AUDIO";
    private MediaRecorder mediaRecorder;
    private String recordFile  ;
    private int seconds;

    private void onBackPressed() {
    }

    public Recording() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recording, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        record_btn = view.findViewById(R.id.record_btn);
        stop_btn = view.findViewById(R.id.stop_button);
        time = view.findViewById(R.id.time);
        record_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);
        record_btn.setImageDrawable(getResources().getDrawable(R.drawable.pause,null));
        startRecording();
                final Handler handler = new Handler();
        handler.post(r = new Runnable() {
            @Override
            public void run() {
                int hr = (seconds/100)/(60*60);
                int min = ((seconds/100)%3600)/60;
                int sec = (seconds/100)%60;
                int mil = (seconds)%100;
                String timeText = String.format(Locale.getDefault(),"%d:%02d:%02d:%02d",hr,min,sec,mil);
                time.setText(timeText);
                if (run) {
                    seconds++;
                }
                handler.postDelayed(this,10);
            }
        });
        run = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stop_button:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Bạn muốn dừng ghi âm")
                                .setPositiveButton("có", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        stopRecording();
                                        requireActivity().onBackPressed();

                                    }
                                })
                                        .setNegativeButton("không", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                builder.show();

            case R.id.record_btn:
                if (isRecording == true){
                    record_btn.setImageDrawable(getResources().getDrawable(R.drawable.play_button,null));
//                    mediaRecorder.pause();
                    run = false;
                    isRecording = false;
                }else {
                    record_btn.setImageDrawable(getResources().getDrawable(R.drawable.pause,null));
//                    mediaRecorder.resume();
//                    Log.d(TAG, "onClick: resume");
                    isRecording = true;
                }
        }
    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private void startRecording() {
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_mm_dd_hh_mm_ss", new Locale("vi","VI"));
                Date now = new Date();
                recordFile = "Recording_"+formatter.format(now)+".3gp";


                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setOutputFile(recordPath +"/" +recordFile);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(),checkPermission )== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{checkPermission},PERMISSION_CODE);
            return false;
        }
    }
}