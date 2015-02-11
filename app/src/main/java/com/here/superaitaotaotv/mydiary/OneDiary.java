package com.here.superaitaotaotv.mydiary;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;

/**
 * Created by superaitaotaoTV on 10/10/14.
 */
public class OneDiary {

    private String id;
    private boolean fromCloud;
    public OneDiary(){
        id = UUID.randomUUID().toString();
        fromCloud = false;
    }
    private String audioPath,photoPath, text;
    private Date date;

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }


    public void removePhoto(){
        if(photoPath!=null){
            photoPath= null;
        }
    }

    public void removeAudio(){
        if(audioPath!=null){
            audioPath = null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id){
        this.id = id;
    }
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath){
        this.photoPath = photoPath;
    }

    public boolean isFromCloud() {
        return fromCloud;
    }

    public void setFromCloud(boolean fromCloud) {
        this.fromCloud = fromCloud;
    }
}
