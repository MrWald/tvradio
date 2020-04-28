package com.fomin.tvradio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class Radio {

    private final static String TAG = Radio.class.getSimpleName();
    //Media session tag used for now playing card.
    private final static String MEDIA_SESSION_TAG = "com.fomin.tvradio.MediaSession";

    private static Radio mInstance;

    private MediaPlayer mMediaPlayer;

    private Context mContext;

    private boolean mInitPlayer;

    private String mCurrentStream = "";

    private MediaSession mMediaSession;

    private String mTitle;

    private String mRadioIcon;

    public static Radio getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Radio(context);
        }
        return mInstance;
    }

    private void initMediaSession() {
        mMediaSession = new MediaSession(mContext, MEDIA_SESSION_TAG);
        mMediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                // Consume the media button event here. Should not send it to other apps.
                return true;
            }
        });

        mMediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);

        if (!mMediaSession.isActive()) {
            mMediaSession.setActive(true);
        }
    }

    private Radio(Context context) {

        mContext = context;

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    boolean load(String streamUrl, String radioIcon, String title, Context context) {
        mContext = context;
        if (!mCurrentStream.equals(streamUrl)) {
            mMediaPlayer.reset();
            mCurrentStream = streamUrl;
            mRadioIcon = radioIcon;
            mTitle = title;
            new Player().execute(streamUrl);
            return true;
        }
        return false;
    }

    void playPause(String streamUrl) {

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            updateMediaSession(PlaybackState.STATE_PAUSED);
        } else {
            if (!mInitPlayer) {
                new Player().execute(streamUrl);
            } else {
                if (!mMediaPlayer.isPlaying()) {
                    mMediaPlayer.start();
                    updateMediaSession(PlaybackState.STATE_PLAYING);
                }
            }
        }
    }

    boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public String getStreamUrl() {
        return mCurrentStream;
    }

    public void closePlayer() {
        if (mMediaSession != null) {
            mMediaSession.setActive(false);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    //Preparing media player will take sometime to buffer the content so prepare it inside the
    //background thread and starting it on UI thread
    static class Player extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog progress;

        @Override
        protected Boolean doInBackground(String... params) {
            boolean prepared;
            try {
                Radio radio = Radio.mInstance;
                radio.mMediaPlayer.setDataSource(params[0]);

                radio.mMediaPlayer.setOnCompletionListener(mp -> {
                    radio.mInitPlayer = false;
                    radio.mMediaPlayer.stop();
                    radio.mMediaPlayer.reset();
                });
                radio.mMediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException | SecurityException |
                    IllegalStateException | IOException e) {
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Radio radio = Radio.mInstance;

            radio.mMediaPlayer.start();

            if (radio.mMediaSession == null) {
                radio.initMediaSession();
            }
            AtomicBoolean finished = new AtomicBoolean(false);

            new Thread(() -> {
                radio.updateMediaSession(PlaybackState.STATE_PLAYING);
                finished.set(true);
            }).start();
            while(!finished.get()){
                try {
                    Log.d(TAG,"Waiting for the media session to be updated");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            radio.mInitPlayer = true;
        }

        Player() {
            Radio radio = Radio.mInstance;
            progress = new ProgressDialog(radio.mContext);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Radio radio = Radio.mInstance;
            this.progress.setMessage(radio.mContext.getString(R.string.buffering));
            this.progress.show();
        }
    }

    private void updateMediaSession(int state) {
        if(!mInitPlayer) {
            MediaMetadata.Builder mediaBuilder;

            Bitmap bitmap = null;
            try {
                URL url = new URL(mRadioIcon);
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                Log.e(TAG, "BitmapFactory.decodeStream", e);
            }

            mediaBuilder = new MediaMetadata.Builder();
            mediaBuilder.putString(MediaMetadata.METADATA_KEY_TITLE, mTitle);
            if (bitmap != null) {
                mediaBuilder.putBitmap(MediaMetadata.METADATA_KEY_ART, bitmap);
            }

            mMediaSession.setMetadata(mediaBuilder.build());
        }

        PlaybackState.Builder stateBuilder = new PlaybackState.Builder();
        stateBuilder.setState(state, 0, 1.0f);

        mMediaSession.setPlaybackState(stateBuilder.build());
    }
}
