package com.example.soundrecorder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class RecordFragment extends Fragment implements View.OnClickListener {
    private NavController navController;
    private ImageButton listBtn;
    private ImageButton record_btn;
    private TextView time;
    private boolean run=false;
    private Runnable r;
    private boolean isRecording= false;
    private  int PERMISSION_CODE = 21;
    private ImageButton cancel,stop;
    private  String recordPermission = Manifest.permission.RECORD_AUDIO;
    private MediaRecorder mediaRecorder;
    private String recordFile  ;
    private int seconds;

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        listBtn = view.findViewById(R.id.record_list_btn);
        record_btn = view.findViewById(R.id.record_btn);
        time = view.findViewById(R.id.time);
        cancel = view.findViewById(R.id.cancel_button);
        stop = view.findViewById(R.id.stop_button);
        listBtn.setOnClickListener(this);
        record_btn.setOnClickListener(this);
        cancel.setVisibility(View.INVISIBLE);
        stop.setVisibility(View.INVISIBLE);
//        final Handler handler = new Handler();
//        handler.post(r = new Runnable() {
//            @Override
//            public void run() {
//                int hr = (seconds/100)/(60*60);
//                int min = ((seconds/100)%3600)/60;
//                int sec = (seconds/100)%60;
//                int mil = (seconds)%100;
//                String timeText = String.format(Locale.getDefault(),"%d:%02d:%02d:%02d",hr,min,sec,mil);
//                time.setText(timeText);
//                if (run) {
//                    seconds++;
//                }
//                handler.postDelayed(this,10);
//            }
//        });
    }
    public void onClick(View v){
        switch (v.getId()){
            case R.id.record_list_btn:
                navController.navigate(R.id.action_recordFragment_to_audioListFragment);
                break;
            case R.id.record_btn:
//                if (isRecording){
//                    //Stop Recording
//                    stopRecording();
//                    record_btn.setImageDrawable(getResources().getDrawable(R.drawable.pause,null));
//                    isRecording=false;
//                    run = true;
//                    cancel.setVisibility(View.VISIBLE);
//                    stop.setVisibility(View.VISIBLE);
//                }else {
                    // Start Recording
                    if(checkPermissions()){
//                        startRecording();
                        record_btn.setImageDrawable(getResources().getDrawable(R.drawable.pause,null));
                        isRecording=true;
                        run = false;
                        navController.navigate(R.id.action_recordFragment_to_recording);
                    }
//        }
    }

//    private void stopRecording() {
//        mediaRecorder.stop();
//        mediaRecorder.release();
//        mediaRecorder = null;
//
//
    }
//
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
        if (ActivityCompat.checkSelfPermission(getContext(),recordPermission )== PackageManager.PERMISSION_GRANTED){
            return true;
        }else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission},PERMISSION_CODE);
            return false;
        }
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        stopRecording();
//    }
}
