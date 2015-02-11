package com.here.superaitaotaotv.mydiary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;
import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by superaitaotaoTV on 12/10/14.
 */
public class DiaryOnePage extends Fragment {

    final static String PARSE_OBJECT_NAME = "SharedDiaries";

    Button playPauseButton, restartPlayButton, editButton, backButton, shareButton;
    TextView dateTextView;
    EditText contentEditText;
    ImageView photoImageView;
    AudioPlayer audioPlayer;
    Boolean isPlaying;
    String audioPath, id, content;
    Date date;
    Bitmap bmp;
    OneDiary oneDiary;
    DiaryDB diaryDB;

    ParseObject parseObject;
    ParseFile photoFile, audioFile;

    boolean isEditing, notAlreadyOnParse;

    DateFormat[] df = new DateFormat[]{
            DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA),
            DateFormat.getDateTimeInstance(),
            DateFormat.getTimeInstance(),
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.dairy_one_page_fragment, null);
        restartPlayButton = (Button)v.findViewById(R.id.restart_play_button);
        playPauseButton = (Button)v.findViewById(R.id.play_pause_button);
        contentEditText = (EditText)v.findViewById(R.id.content_edit_text);
        dateTextView = (TextView)v.findViewById(R.id.date_text_view);
        photoImageView = (ImageView)v.findViewById(R.id.photo_image_view);
        backButton = (Button)v.findViewById(R.id.back_button);
        editButton = (Button)v.findViewById(R.id.edit_diary_button);
        shareButton = (Button)v.findViewById(R.id.share_button);

        id =  getActivity().getIntent().getStringExtra("id");
        diaryDB = new DiaryDB(getActivity().getApplicationContext());
        try {
            oneDiary = diaryDB.getOneDiary(id);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        content = oneDiary.getText();
        contentEditText.setText(content);
        contentEditText.setFocusable(false);

        date = oneDiary.getDate();
        dateTextView.setText(df[0].format(date));

        String photoPath = oneDiary.getPhotoPath();
        if(photoPath!=null){
            bmp = BitmapFactory.decodeFile(photoPath);
        }
        if(bmp!=null) {
            bmp = Rounder.getRoundedShape(bmp, 100 , 100);
            photoImageView.setImageBitmap(bmp);
        }

        audioPath = oneDiary.getAudioPath();
        audioPlayer = new AudioPlayer(getActivity().getApplicationContext());
        isPlaying = false;
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying) {
                    audioPlayer.pause();
                    playPauseButton.setBackgroundResource(R.drawable.play_icon);
                    isPlaying = !isPlaying;
                } else
                {
                    try {
                        audioPlayer.resume(audioPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(audioPath!=null) {
                        playPauseButton.setBackgroundResource(R.drawable.pause_play_icon);
                        isPlaying = !isPlaying;
                    }
                }

            }
        });

        restartPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(audioPath!=null){
                    try {
                        audioPlayer.play(audioPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    playPauseButton.setBackgroundResource(R.drawable.pause_play_icon);
                    isPlaying = true;
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),DiaryList.class);
                startActivity(i);
            }
        });

        isEditing = false;

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEditing){
                    editButton.setText(getActivity().getString(R.string.diary_page_edit));
                    contentEditText.setFocusable(false);
                    String content = contentEditText.getText().toString();
                    diaryDB.updateContent(oneDiary.getId(),content);
                }else{
                    contentEditText.setFocusableInTouchMode(true);
                    editButton.setText(getActivity().getString(R.string.diary_page_done));
                    //contentEditText.requestFocus();
                }
                isEditing = !isEditing;
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        //.setView(v) The child already has a parent
                        .setTitle(getActivity().getString(R.string.share_diglog_title))
                        .setMessage(getActivity().getString(R.string.share_dialog_message))
                        .setPositiveButton(getActivity().getString(R.string.share_dialog_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                checkBeforeSaveToParse();
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.share_dialog_no), null)
                        .setIcon(R.drawable.ha_ha)
                        .show();
            }
        });

        return v;

    }

    private void checkBeforeSaveToParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("SharedDiaries");
        query.whereEqualTo("myId", oneDiary.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if(parseObjects.size()==0 || parseObjects==null) {
                    saveDiaryToParse();            
                }else{
                    Toast.makeText(getActivity(),getActivity().getString(R.string.already_on_parse),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDiaryToParse(){
        Toast.makeText(getActivity(),getActivity().getString(R.string.shared),Toast.LENGTH_SHORT).show();
        parseObject = new ParseObject(PARSE_OBJECT_NAME);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        String imageFileName = oneDiary.getId()+".png";
        photoFile = new ParseFile(imageFileName,byteArray);

        audioPath = oneDiary.getAudioPath();
        if(audioPath!=null) {
            File localAudioFile = new File(audioPath);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                is = new FileInputStream(localAudioFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] temp = new byte[(int) localAudioFile.length()];
            int read;

            try {
                while ((read = is.read(temp)) >= 0) {
                    buffer.write(temp, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] audioData = buffer.toByteArray();
            audioFile = new ParseFile(oneDiary.getId() + ".mp4", audioData);
            parseObject.put("audioFile", audioFile);
        }

        parseObject.put("myId",oneDiary.getId());
        parseObject.put("text",oneDiary.getText());
        parseObject.put("date",df[0].format(oneDiary.getDate()));
        parseObject.put("imageFile",photoFile);
        parseObject.saveInBackground();

        sendParsePush();
    }

    private void sendParsePush(){
        ParsePush push = new ParsePush();
        push.setChannel("Poems");
        push.setMessage("New Poem!");
        push.sendInBackground();
    }

    @Override
    public void onPause() {
        super.onPause();
        audioPlayer.stop();
        Log.i("onPause", "Paused");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("OnDestroy","destroyed");
    }
}
