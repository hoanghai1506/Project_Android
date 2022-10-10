package com.example.soundrecorder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.File;
import java.io.IOException;


public class AudioListFragment extends Fragment implements AudioListAdapter.onItemListClick {
    private ConstraintLayout player_list;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView audio_listview;
    private File[] allFile;
    private AudioListAdapter audioListAdapter;
    private MediaPlayer mediaPlayer=null;
    private boolean isStarting = false;
    private File fileToPlay;
    private ImageButton playBtn;
    private TextView playerHeader , playerName;
    private SeekBar playerSeekbar;
    private Handler seekbarHandler;
    private Runnable updateSeekbar;
    public AudioListFragment() {

    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_audio_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        player_list = view.findViewById(R.id.player_list);
        bottomSheetBehavior = BottomSheetBehavior.from(player_list);
        audio_listview = view.findViewById(R.id.audio_listview);
        playBtn = view.findViewById(R.id.player_play_btn);
        playerHeader = view.findViewById(R.id.player_header_title);
        playerName = view.findViewById(R.id.audioName);


        playerSeekbar = view.findViewById(R.id.player_seek_bar);

        String path = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(path);
        allFile = directory.listFiles();
        audioListAdapter = new AudioListAdapter(allFile,this);
        audio_listview.setHasFixedSize(true);
        audio_listview.setLayoutManager(new LinearLayoutManager(getContext()));
        audio_listview.setAdapter(audioListAdapter);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStarting){
                    pauseAudio();
                }else {
                    if (fileToPlay != null){
                        resumeAudio();
                    }
                }
            }
        });

        playerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseAudio();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mediaPlayer.seekTo(progress);
                resumeAudio();
            }
        });
    }

    @Override
    public void onClickListener(File file, int position) {
        fileToPlay = file;
        if(isStarting){
            stopAudio();
        }else {
            fileToPlay = file;
            playAudio(fileToPlay);
        }
    }

    @Override
    public void onLongClickListener(File file, int position) {
            fileToPlay = file;
        AlertDialog.Builder delete = new AlertDialog.Builder(getActivity());
        delete.setMessage("Bạn có muốn xóa không")
                        .setPositiveButton("có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteAudio(fileToPlay);
                                requireActivity().onBackPressed();
                            }
                        })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        delete.show();
    }

    private void pauseAudio(){
        mediaPlayer.pause();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.phay_btn,null));
        isStarting = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }
    private void resumeAudio(){
        mediaPlayer.start();
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_btn,null));
        isStarting = true;
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);
    }
    private void stopAudio() {
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.phay_btn,null));
        playerHeader.setText("Tạm dừng");
        mediaPlayer.stop();
        isStarting = false;
        seekbarHandler.removeCallbacks(updateSeekbar);
    }

    private void playAudio(File fileToPlay) {
        mediaPlayer = new MediaPlayer();

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        playBtn.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.pause_btn,null));
        playerName.setText(fileToPlay.getName());
        playerHeader.setText("Đang phát");
        isStarting=true;
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stopAudio();
                playerHeader.setText("Hoàn thành");
            }
        });
        playerSeekbar.setMax(mediaPlayer.getDuration());

        seekbarHandler = new Handler();
        updateRunnable();
        seekbarHandler.postDelayed(updateSeekbar,0);
    }

    private void updateRunnable() {
        updateSeekbar = new Runnable() {
            @Override
            public void run() {
                playerSeekbar.setProgress(mediaPlayer.getCurrentPosition());
                seekbarHandler.postDelayed(this,500);

            }
        };
    }
    private void deleteAudio(File fileToPlay){
        String file= fileToPlay.getAbsolutePath();
        try {
            if (checkExternalMedia()){

               File file2 = new File(file);
                if (file2.exists()){
                    file2.delete();
                }
            }else {}
        }catch (Exception e){

        }
    }
    private boolean checkExternalMedia() {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        return mExternalStorageAvailable && mExternalStorageWriteable;
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        stopAudio();
//    }
}