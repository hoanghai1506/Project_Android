package com.example.soundrecorder;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Objects;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder>  {

    private File[] allFile;
    private TimeAgo timeAgo;
    private onItemListClick onItemListClick;

    public AudioListAdapter(File[] allFile , onItemListClick onItemListClick) {
        this.allFile = allFile;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item,parent,false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        holder.list_title.setText(allFile[position].getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFile[position].lastModified()));
    }

    @Override
    public int getItemCount() {
        return allFile.length;
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;
        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.title);
            list_date = itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }


        @Override
        public void onClick(View view) {
            onItemListClick.onClickListener(allFile[getAdapterPosition()],getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            onItemListClick.onLongClickListener(allFile[getAdapterPosition()],getAdapterPosition());
            return false;
        }
    }
    public interface onItemListClick {
        void onClickListener(File file, int position);
        void onLongClickListener(File file,int position);
    }


}
