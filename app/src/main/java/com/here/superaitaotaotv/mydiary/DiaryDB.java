package com.here.superaitaotaotv.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by superaitaotaoTV on 9/10/14.
 */
public class DiaryDB extends SQLiteOpenHelper {

    private Context mContext;
    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "diaryDB.db";
    private static final String TABLE_NAME = "diaries";

    DateFormat[] df = new DateFormat[]{
            DateFormat.getDateInstance(DateFormat.LONG, Locale.CHINA),
            DateFormat.getDateTimeInstance(),
            DateFormat.getTimeInstance(),
    };

    public DiaryDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "Create Table IF NOT EXISTS " + TABLE_NAME + "(id text, photoPath text, audioPath text, " +
                "date text, content text, fromCloud text)";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {
            sqLiteDatabase.execSQL("ALTER TABLE diaries ADD COLUMN fromCloud text");
        }
    }

        /*public void saveDiaries(ArrayList<OneDiary> manyDiaries) {
            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
            db.execSQL(query);
            onCreate(db);

            for (int i = 0; i < manyDiaries.size(); i++) {

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                Bitmap bmp = manyDiaries.get(i).getPhoto();
                String audioPath = manyDiaries.get(i).getAudioPath();

                ContentValues cv = new ContentValues();
                cv.put("id", i);
                if (bmp != null) {
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    cv.put("image", bytes);
                    Log.d("image", "saved");
                }
                if (audioPath != null) {
                    cv.put("audioPath", manyDiaries.get(i).getAudioPath());
                    Log.d("audio", "saved");
                }

                long j = db.insert(TABLE_NAME, null, cv);
                if (j < 0) {
                    Toast.makeText(mContext, "Error saving", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, manyDiaries.size() + " saved", Toast.LENGTH_SHORT).show();
                }

            }
            db.close();

        }*/

    public void addDiary(OneDiary oneDiary) {

        SQLiteDatabase db = this.getWritableDatabase();
        String photoPath = oneDiary.getPhotoPath();
        String audioPath = oneDiary.getAudioPath();
        String id = oneDiary.getId().toString();
        String text = oneDiary.getText();
        Boolean fromCloud = oneDiary.isFromCloud();
        Date date = oneDiary.getDate();


        ContentValues cv = new ContentValues();
        cv.put("id", id);
        cv.put("content", text);
        cv.put("date",df[0].format(date));
        if(photoPath!=null){
            cv.put("photoPath",photoPath);
        }
        if (audioPath != null) {
            cv.put("audioPath", audioPath);
            Log.d("audio", "saved");
        }
        if (fromCloud){
            cv.put("fromCloud","1");
        }else{
            cv.put("fromCloud","0");
        }
        long j = db.insert(TABLE_NAME, null, cv);

        db.close();

    }

    public void addPhoto(String id, String photoPath){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("photoPath", photoPath);
        Log.i("addPhoto",photoPath);
        db.update(TABLE_NAME, cv, "id" + "= ?", new String[] {id});
        db.close();
    }

    public void addAudio(String id, String audioPath){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("audioPath", audioPath);
        Log.i("AddAudio",audioPath);
        db.update(TABLE_NAME, cv, "id" + "= ?", new String[] {id});
        db.close();
    }

    public void updateContent(String id, String content){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("content", content);
        db.update(TABLE_NAME, cv, "id" + "= ?", new String[] {id});
        db.close();
    }


    public ArrayList<OneDiary> searchDiaries(String text) throws ParseException {

        ArrayList<OneDiary> manyDiaries = new ArrayList<OneDiary>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "Select * from " + TABLE_NAME +" where content like '%" + text+ "%'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            Log.d("Cursor", "searching");

            do {
                OneDiary oneDiary = new OneDiary();

                String photoPath = cursor.getString(1);
                if(photoPath!=null){
                   oneDiary.setPhotoPath(photoPath);
                }

                String audioPath = cursor.getString(2);
                if (audioPath != null) {
                    oneDiary.setAudioPath(cursor.getString(2));
                }
                oneDiary.setDate(df[0].parse(cursor.getString(3)));
                oneDiary.setText(cursor.getString(4));

                oneDiary.setId( cursor.getString(0));
                //Although OneDiary always generate a random ID for each OneDiary, Id needs to be retrieved from the DB to e
                //ensure Id consistence
                if(cursor.getInt(5)==1){
                    oneDiary.setFromCloud(true);
                }
                manyDiaries.add(oneDiary);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return manyDiaries;
    }

    public ArrayList<OneDiary> getDiaries() throws ParseException {



        ArrayList<OneDiary> manyDiaries = new ArrayList<OneDiary>();
        SQLiteDatabase db = this.getReadableDatabase();

        Log.i("DBVersion",db.getVersion()+"");

        String selectQuery = "Select * from " + TABLE_NAME;
        Cursor cursor = null;
        cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                OneDiary oneDiary = new OneDiary();
                String photoPath = cursor.getString(1);
                if(photoPath!=null){
                    oneDiary.setPhotoPath(photoPath);
                }
                String audioPath = cursor.getString(2);
                if (audioPath != null) {
                    oneDiary.setAudioPath(cursor.getString(2));
                }
                oneDiary.setDate(df[0].parse(cursor.getString(3)));
                oneDiary.setText(cursor.getString(4));

                oneDiary.setId(cursor.getString(0));
                //Although OneDiary always generate a random ID for each OneDiary, Id needs to be retrieved from the DB to e
                //ensure Id consistence
                if(cursor.getString(5).equals("1")){
                    oneDiary.setFromCloud(true);
                }
                manyDiaries.add(oneDiary);
            } while (cursor.moveToNext());

        }
        cursor.close();
        db.close();

        return manyDiaries;
    }

    public OneDiary getOneDiary(String id) throws ParseException {

        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "Select * from " + TABLE_NAME + " where id = '" + id + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        OneDiary oneDiary = new OneDiary();

        Log.d("Cursor", "no");
        if (cursor.moveToFirst()) {
            do {
                Log.d("Cursor", "aaaa");
                byte[] bytes = cursor.getBlob(1);
                String photoPath = cursor.getString(1);
                if(photoPath!=null){
                    oneDiary.setPhotoPath(photoPath);
                }
                String audioPath = cursor.getString(2);
                if (audioPath != null) {
                    oneDiary.setAudioPath(cursor.getString(2));
                }
                oneDiary.setDate(df[0].parse(cursor.getString(3)));
                oneDiary.setText(cursor.getString(4));

                oneDiary.setId(cursor.getString(0));

                if(cursor.getString(5).equals("1")){
                    oneDiary.setFromCloud(true);
                }
                //Although OneDiary always generate a random ID for each OneDiary, Id needs to be retrieved from the DB to e
                //ensure Id consistence
            } while (cursor.moveToNext());
        }
        cursor.close();
        return oneDiary;
    }

    public boolean alreadyHasDiary(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "Select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.d("Cursor", "no");
        if (cursor.moveToFirst()) {
            do {
                if(cursor.getString(0).equals(id)){
                    Log.i("checking id","alreadyHas");
                    return true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return false;
    }

    public void deleteDiary(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "id = '" + id + "'", null);
        db.close();
    }

        /*public void saveImages(ArrayList<Bitmap> images){

            SQLiteDatabase db = this.getWritableDatabase();
            String query = "DROP TABLE IF EXISTS "+TABLE_NAME;
            db.execSQL(query);
            onCreate(db);
            Log.d("saveImages", "no");
            for(int i=0; i<images.size(); i++){
                Log.d("saveImages","yes");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                images.get(i).compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();
                ContentValues cv=new ContentValues();
                cv.put("image", bytes);

                long j = db.insert(TABLE_NAME, null, cv);
                if(j<0){
                    Toast.makeText(mContext, "Error saving", Toast.LENGTH_SHORT).show();
                }
            }

        Bitmap bmp = images.get(i);
            int size = bmp.getRowBytes() * bmp.getHeight();
            ByteBuffer b = ByteBuffer.allocate(size); bmp.copyPixelsToBuffer(b);
            byte[] bytes = new byte[size];
            b.get(bytes, 0, bytes.length);


        public ArrayList<Bitmap> getImages(){

            ArrayList<Bitmap> images = new ArrayList<Bitmap>();
            SQLiteDatabase db = this.getReadableDatabase();

            String selectQuery = "Select * from "+TABLE_NAME;
            Cursor cursor = db.rawQuery(selectQuery,null);

            int i = 0;
            Log.d("Cursor","no");
            if(cursor.moveToFirst()){
                do{
                    Log.d("Cursor","getImage");
                    byte[] bytes = cursor.getBlob(1);
                    images.add(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    i++;
                }while(cursor.moveToNext());
            }

            return images;
        }*/
}