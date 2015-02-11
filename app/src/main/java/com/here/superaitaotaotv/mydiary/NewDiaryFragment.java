package com.here.superaitaotaotv.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by superaitaotaoTV on 9/10/14.
 */
public class NewDiaryFragment extends Fragment {

    Button addPhotoButton, addAudioButton,playPauseButton,restartPlayButton,deleteAudioButton,deletePhotoButton,
        addDiaryButton, cancelButton, datePickerButton;
    ImageView imageView;
    EditText textEditText;

    OneDiary oneDiary;
    Bitmap bitmap;
    String audioPath,photoPath;
    Date date;
    Uri mImageCaptureUri;

    static final int REQUEST_DATE = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PICK_FROM_FILE = 2;

    AudioRecorder audioRecorder;
    AudioPlayer audioPlayer;

    Boolean isPlaying, isRecording;
    DiaryDB diaryDB;

    DateFormat[] df = new DateFormat[] {
            DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA),
            DateFormat.getDateTimeInstance(),
            DateFormat.getTimeInstance(),
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_diary, null);
        addAudioButton = (Button) v.findViewById(R.id.record_audio_button);
        addPhotoButton = (Button) v.findViewById(R.id.take_photo_button);
        deleteAudioButton = (Button)v.findViewById(R.id.cancel_audio_button);
        deletePhotoButton = (Button)v.findViewById(R.id.cancel_photo_taking_button);
        addDiaryButton = (Button)v.findViewById(R.id.add_diary_button);
        cancelButton = (Button)v.findViewById(R.id.cancel_new_diary_button);
        imageView = (ImageView)v.findViewById(R.id.photo_image_view);
        playPauseButton = (Button) v.findViewById(R.id.play_pause_button);
        restartPlayButton = (Button) v.findViewById(R.id.restart_button);

        textEditText = (EditText)v.findViewById(R.id.text_edit_text);
        datePickerButton = (Button)v.findViewById(R.id.date_picker_button);

        photoPath = "";

        date = new Date(System.currentTimeMillis());
        datePickerButton.setText(df[0].format(date));

        oneDiary = new OneDiary();
        diaryDB = new DiaryDB(getActivity().getApplicationContext());

        bitmap = BitmapFactory.decodeResource(getResources(),GetRandomPic.getRandomPic());
        imageView.setImageBitmap(Rounder.getRoundedShape(bitmap, 300, 300));

        final String [] items = new String [] {getActivity().getString(R.string.select_image_dialog_camera), getActivity().getString(R.string.select_image_dialog_SD)};
        ArrayAdapter<String> adapter  = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getActivity().getString(R.string.select_image_dialog_title));
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) {
                if (item == 0) {
                    dispatchTakePictureIntent();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();

        addPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });


        audioRecorder = new AudioRecorder(getActivity().getApplicationContext());
        audioPlayer = new AudioPlayer(getActivity().getApplicationContext());
        audioPath = null;

        isPlaying = false;

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioRecorder.stopRecording();
                isRecording = false;
                addAudioButton.setBackgroundResource(R.drawable.record_icon);
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
                audioRecorder.stopRecording();
                isRecording = false;
                addAudioButton.setBackgroundResource(R.drawable.record_icon);
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

        isRecording = false;



        addAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( isRecording ){
                    audioRecorder.stopRecording();
                    addAudioButton.setBackgroundResource(R.drawable.record_icon);
                } else{
                    audioPlayer.stop();
                    isPlaying = false;
                    playPauseButton.setBackgroundResource(R.drawable.play_icon);

                    try {
                        audioPath = audioRecorder.startRecording(oneDiary.getId().toString()+".mp4");
                    } catch (IOException e) {
                        Log.d("recorder","failed");
                        e.printStackTrace();
                    }
                    addAudioButton.setBackgroundResource(R.drawable.stop_record_icon);
                }
                isRecording = !isRecording;
            }
        });

        deleteAudioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                audioRecorder.stopRecording();
                audioPlayer.stop();
                if( audioPath!=null){
                    audioRecorder.deleteAudio();
                    audioPath = null;
                    Toast.makeText(getActivity().getApplicationContext(),"Audio Deleted",Toast.LENGTH_SHORT).show();
                }
            }
        });

        deletePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !(bitmap==null) ){
                    bitmap = null;
                    imageView.setImageResource(R.drawable.ha_ha);
                }
            }
        });

        addDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveNewDiary();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                returnToMainActivity();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( !(audioPath==null)){
                    audioRecorder.deleteAudio();
                }
                returnToMainActivity();
            }
        });



        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
                DialogFragment fragment = DatePickerFragment.newInstance(new Date());
                fragment.setTargetFragment(NewDiaryFragment.this, REQUEST_DATE);
                fragment.show(fm,null);
            }
        });
        return v;
    }

    public void saveNewDiary() throws IOException {
        oneDiary.setAudioPath(audioPath);
        savePhoto();
        oneDiary.setText(textEditText.getText().toString());
        oneDiary.setDate(date);
        diaryDB.addDiary(oneDiary);
    }

    public void savePhoto() throws IOException {
        if(bitmap!=null){
            photoPath = oneDiary.getId().toString();
            FileOutputStream fos = getActivity().openFileOutput(photoPath, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            photoPath = getActivity().getFilesDir() + "/" + photoPath;
            oneDiary.setPhotoPath(photoPath);
            //fos.flush();
            fos.close();
        }
    }

    public void returnToMainActivity(){
        Intent i = new Intent(getActivity(),DiaryList.class);
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == MyActivity.RESULT_OK) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(Rounder.getRoundedShape(bitmap, 300, 300));
        }

        if (requestCode == REQUEST_DATE & resultCode == MyActivity.RESULT_OK) {
            date = (Date) data.getSerializableExtra(DatePickerFragment.Extra_Date);
            datePickerButton.setText(df[0].format(date));
        }
        if (requestCode == PICK_FROM_FILE & resultCode == MyActivity.RESULT_OK) {
            String path;
            mImageCaptureUri = data.getData(); //from Gallery
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = null;
            cursor  = getActivity().getContentResolver().query(
                    mImageCaptureUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();
            bitmap = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(Rounder.getRoundedShape(bitmap, 300, 300));
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        diaryDB.close();
        audioPlayer.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
