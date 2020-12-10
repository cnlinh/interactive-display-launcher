package com.example.leochris.launcher;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeTab extends Fragment {

    VideoView videoView;

    public WelcomeTab() {
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
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        videoView = (VideoView) view.findViewById(R.id.welcome_video);
        Uri uri = Uri.parse("android.resource://"+getActivity().getPackageName()+"/"+R.raw.campus);
        videoView.setVideoURI(uri);

        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                videoView.start();
                mp.start();
            }
        });

//        ImageView imageView = (ImageView) view.findViewById(R.id.welcome_image);
//        Glide.with(this).load(R.raw.campus_img).into(imageView);

        return view;
    }

}
