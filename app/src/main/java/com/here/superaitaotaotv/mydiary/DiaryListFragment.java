package com.here.superaitaotaotv.mydiary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by superaitaotaoTV on 6/10/14.
 */
public class DiaryListFragment extends Fragment {

    final static String PARSE_OBJECT_NAME = "SharedDiaries";

    ListView diaryList;
    ArrayAdapter<OneDiary> adapter;
    Button addDiaryButton, searchButton;
    ArrayList<OneDiary> manyDiaries;
    DiaryDB diaryDB;
    EditText searchEditText;
    Boolean editingSearch;

    View v;

    DateFormat[] df = new DateFormat[] {
            DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA),
            DateFormat.getDateTimeInstance(),
            DateFormat.getTimeInstance(),
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //setHasOptionsMenu(true);
        Parse.initialize(getActivity(), "ec3wxTIPtU9zu1g6Nft0zPG4tGiDawXi8qHZ84f7", "vOebVT2D0x4UJHGDLabVy7yXZGEkijQqrErCz6cv");

        v = inflater.inflate(R.layout.activity_my, null);
        diaryList = (ListView) v.findViewById(R.id.list_view);
        addDiaryButton = (Button) v.findViewById(R.id.add_diary_button);
        searchButton = (Button)v.findViewById(R.id.search_button);
        searchEditText = (EditText)v.findViewById(R.id.search_edit_text);

        diaryDB = new DiaryDB(getActivity().getApplicationContext());
        try {
            manyDiaries = diaryDB.getDiaries();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (manyDiaries == null) {
            Toast.makeText(getActivity().getApplicationContext(),getActivity().getString(R.string.diary_list_no_diary_found), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), manyDiaries.size()+ getActivity().getString(R.string.diary_list_diaries_found), Toast.LENGTH_SHORT).show();
        }

        if (getActivity().getIntent().getBooleanExtra("NewMessage",false)){
            sendParsePush();
            getNewDiariesFromParse();
        }

        adapter = new MyAdapter(getActivity().getApplicationContext(), R.layout.listview_row, manyDiaries);
        diaryList.setAdapter(adapter);

        editingSearch = false;

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if((searchEditText.getText().toString().equals("")) && (editingSearch)){
                    try {
                        manyDiaries = diaryDB.getDiaries();
                        adapter.notifyDataSetChanged();
                        editingSearch = false;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchEditText.getText().toString();
                if(searchText.equals("")){
                    Toast.makeText(getActivity(),getActivity().getString(R.string.diary_list_search_no_text), Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        manyDiaries = diaryDB.searchDiaries(searchText);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    editingSearch = true;
                    if(manyDiaries.size()==0){
                            Toast.makeText(getActivity(),getActivity().getString(R.string.diary_list_search_no_found), Toast.LENGTH_SHORT).show();
                        }else {
                        adapter.notifyDataSetChanged();
                        Log.d("SearchResult", manyDiaries.get(0).getText());
                    }


                }
            }
        });

        addDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(),NewDiary.class);
                startActivity(i);
            }
        });

        diaryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),DiaryOnePageActivity.class);
                intent.putExtra("id",manyDiaries.get(i).getId().toString());
                startActivity(intent);
            }
        });

        diaryList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int position = i;

                new AlertDialog.Builder(getActivity())
                        //.setView(v) The child already has a parent
                        .setTitle(getActivity().getString(R.string.diary_list_alert_delete_title))
                        .setMessage(getActivity().getString(R.string.diary_list_alert_delete))
                        .setPositiveButton(getActivity().getString(R.string.alert_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                diaryDB.deleteDiary(manyDiaries.get(position).getId().toString());
                                OneDiary oneDiary = manyDiaries.get(position);

                                //deleteAudio&Photo
                                if (oneDiary.getAudioPath() != null) {
                                    File file = new File(oneDiary.getAudioPath());
                                    if (file != null) {
                                        boolean audioDeleted = file.delete();
                                    }
                                }

                                if (oneDiary.getPhotoPath() != null) {
                                    File file = new File(oneDiary.getPhotoPath());
                                    if (file != null) {
                                        boolean photoDeleted = file.delete();
                                    }
                                }

                                manyDiaries.remove(position);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getActivity().getApplicationContext(),getActivity().getString(R.string.diary_list_diary_deleted), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(getActivity().getString(R.string.alert_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getActivity().getApplicationContext(), getActivity().getString(R.string.diary_list_nothing_deleted), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setIcon(R.drawable.alert_icon)
                        .show();

                return true;
            }
        });

        return v;
    }

    private void sendParsePush(){
        ParsePush push = new ParsePush();
        push.setChannel("FromDaShu");
        push.setMessage("收到了：）");
        push.sendInBackground();
    }

    private class MyAdapter extends ArrayAdapter<OneDiary> {

        private MyAdapter(Context context, int resource, ArrayList<OneDiary> manyDiaries) {
            super(context, resource, manyDiaries);

        }

        @Override
        public int getCount() {
            return manyDiaries.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_row, null);
            TextView textView = (TextView) convertView.findViewById(R.id.content_summary);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.photo_image_view);
            ImageView audioImageView = (ImageView)convertView.findViewById(R.id.got_audio_image);
            TextView dateTextView = (TextView)convertView.findViewById(R.id.date_text_view);
            ImageView fromCloudImageview = (ImageView)convertView.findViewById(R.id.from_cloud_image);

            OneDiary oneDiary = manyDiaries.get(position);

            textView.setText(oneDiary.getText());

            String photoPath = oneDiary.getPhotoPath();
            if (photoPath!=null){
            Log.i("Here",photoPath);}
            Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
            if(bitmap!=null) {
                bitmap = Rounder.getRoundedShape(bitmap,80,80);
                imageView.setImageBitmap(bitmap);
            }

            Date date = oneDiary.getDate();
            dateTextView.setText(df[0].format(date));

            String audioPath = oneDiary.getAudioPath();

            audioImageView.setImageResource(R.drawable.no_audio);
            if(audioPath!=null)
            {
                audioImageView.setImageResource(R.drawable.audio);
            }

            if(oneDiary.isFromCloud()){
                fromCloudImageview.setImageResource(R.drawable.from_cloud_round);
            }

            return convertView;
        }
    }

    private void getNewDiariesFromParse(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_OBJECT_NAME);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {
                if (parseObjects == null) return;
                if (parseObjects.size() > 0) {
                    for (int i = 0; i < parseObjects.size(); i++) {
                        ParseObject parseObject = parseObjects.get(i);
                        if (!diaryDB.alreadyHasDiary(parseObject.getString("myId"))) {
                            final OneDiary oneDiary = new OneDiary();

                            oneDiary.setFromCloud(true);
                            oneDiary.setId(parseObject.getString("myId"));
                            oneDiary.setText(parseObject.getString("text"));
                            try {
                                oneDiary.setDate(df[0].parse(parseObject.getString("date")));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }

                            ParseFile imageFile = (ParseFile) parseObject.get("imageFile");
                            if (imageFile!=null) {
                                imageFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, com.parse.ParseException e) {
                                        try {
                                            oneDiary.setPhotoPath(saveBitmapLocally(oneDiary.getId(), bytes));
                                            diaryDB.addPhoto(oneDiary.getId(), oneDiary.getPhotoPath());
                                            adapter.notifyDataSetChanged();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                });
                            }

                            ParseFile audioFile = (ParseFile) parseObject.get("audioFile");
                            if(audioFile!=null) {
                                Log.i(oneDiary.getId(),"Got Audio");
                                audioFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, com.parse.ParseException e) {
                                        try {
                                            oneDiary.setAudioPath(saveAudioLocally(oneDiary.getId() + ".mp4", bytes));
                                            Log.i("oneDiaryAudioPath", oneDiary.getAudioPath());
                                            diaryDB.addAudio(oneDiary.getId(), oneDiary.getAudioPath());
                                            adapter.notifyDataSetChanged();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                });
                            }else{
                                Log.i(oneDiary.getId(),"No Audio");
                            }

                            manyDiaries.add(oneDiary);
                            diaryDB.addDiary(oneDiary);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getActivity(), getActivity().getString(R.string.diary_obtained_from_parse), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });
    }

    private String saveBitmapLocally(String photoPath, byte[] bytes) throws IOException {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        if (bitmap==null){
            Log.i("bitmap", "no");
        }
        String mPhotoPath = null;
        if(bitmap!=null){
            FileOutputStream fos = getActivity().openFileOutput(photoPath, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            mPhotoPath = getActivity().getFilesDir() + "/" + photoPath;
            fos.close();
        }
        return mPhotoPath;
    }

    private String saveAudioLocally(String audioPath, byte[] bytes) throws IOException {
        Log.i("saveAudioLocally","here");
        File audioFile = new File(getActivity().getFilesDir(), audioPath);
        String mAudioPath = audioFile.getAbsolutePath();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(audioFile));
        bos.write(bytes);
        bos.flush();
        bos.close();
        return mAudioPath;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_set_password) {
           Intent i = new Intent(getActivity(),SetPasswordActivity.class);
           startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        diaryDB.close();
    }


}
