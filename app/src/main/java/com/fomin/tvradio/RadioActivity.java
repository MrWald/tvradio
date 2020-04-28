package com.fomin.tvradio;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.squareup.picasso.Picasso;

public class RadioActivity extends FragmentActivity {

    private Radio mSingleton;

    private ImageButton mPlayPause;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String streamUrl = getIntent().getStringExtra("EXTRA_STREAM");
        String iconUrl = getIntent().getStringExtra("EXTRA_ICON");
        String title = getIntent().getStringExtra("EXTRA_TITLE");

        setContentView(R.layout.activity_radio);

        ImageView radioIcon = findViewById(R.id.radio_icon);
        Picasso.get().load(iconUrl).fit().into(radioIcon);

        TextView radioTitle = findViewById(R.id.radio_title);
        radioTitle.setText(title);

        mSingleton = Radio.getInstance(this);

        mPlayPause = findViewById(R.id.play_pause);

        checkPlayerState(false);

        mPlayPause.setOnClickListener(view -> {
            checkPlayerState(true);
            mSingleton.playPause(streamUrl);
        });

        //Check if it's first load
        if (play(streamUrl, iconUrl, title)) {
            mPlayPause.setImageResource(R.drawable.pause);
        }
    }

    private void checkPlayerState(boolean invert) {
        mPlayPause.setImageResource(mSingleton.isPlaying() ?
                (invert ? R.drawable.play : R.drawable.pause) :
                (invert ? R.drawable.pause : R.drawable.play));
    }

    private boolean play(String streamUrl, String radioIcon, String title) {
        return mSingleton.load(streamUrl, radioIcon, title, this);
    }
}
