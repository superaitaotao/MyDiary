package com.here.superaitaotaotv.mydiary;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by superaitaotaoTV on 11/10/14.
 */
public class AudioRecorder {

    private Context mContext;
    private MediaRecorder mRecorder;
    File directory, audioFile;
    String audioPath;

    public AudioRecorder(Context c){
        mContext = c;
        directory = mContext.getFilesDir();
    }

    public String startRecording(String fileName) throws IOException {

        stopRecording();
        Toast.makeText(mContext, "Recording", Toast.LENGTH_SHORT).show();
        audioFile = new File(directory, fileName);
        audioPath = audioFile.getAbsolutePath();

        Log.d("savePath", audioPath);

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(audioPath);
        mRecorder.prepare();
        mRecorder.start();   // Recording is now started

        return audioPath;
    }

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.release();
            Toast.makeText(mContext, "Recording Stopped", Toast.LENGTH_SHORT).show();
        }
        mRecorder = null;
    }

    public void deleteAudio() {
        if (!(audioPath == null)) {
            Toast.makeText(mContext, mContext.getString(R.string.audio_file_deleted), Toast.LENGTH_SHORT).show();
            File file = new File(audioPath);
            boolean deleted = file.delete();
            audioPath = null;
        }
    }

    public boolean isRecorderPresent(){
        return (mRecorder!=null);
    }

}
