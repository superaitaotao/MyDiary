package com.here.superaitaotaotv.mydiary;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;

import java.io.IOException;

public class AudioPlayer {

    private Context mContext;
    private MediaPlayer mPlayer;

    public AudioPlayer(Context c) {
        mContext = c;

    }

    public void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void play(String audioPath) throws IOException {
        stop();
        if (audioPath != null) {
            Log.d("Audio",audioPath);
            Toast.makeText(mContext, "Playing", Toast.LENGTH_SHORT).show();
            //Uri uri = Uri.parse(audioPath);
            mPlayer = new MediaPlayer();
            mPlayer.setDataSource(audioPath);
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                    Log.d("Audio","complete");
                }
            });
            mPlayer.prepare();
            mPlayer.start();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.no_audio_clip), Toast.LENGTH_SHORT).show();
        }
    }

    public void pause(){
        if(mPlayer!=null) {
            if (mPlayer.isPlaying()) {
                mPlayer.pause();
            } else {
                Toast.makeText(mContext, "Not Playing Anything", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void resume(String audioPath) throws IOException {
        if (mPlayer == null) {
            play(audioPath);
        }else{
            mPlayer.start();
        }
    }

}
