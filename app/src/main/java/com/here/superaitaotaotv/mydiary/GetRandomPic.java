package com.here.superaitaotaotv.mydiary;

import com.example.superaitaotaotv.mydiary.R;

import java.util.Random;

/**
 * Created by superaitaotaoTV on 12/10/14.
 */
public class GetRandomPic {

    private static int[] pictures = {
        R.drawable.random1,
                R.drawable.random2,
                R.drawable.random3,
                R.drawable.random4,
                R.drawable.random5,
                R.drawable.random6,
                R.drawable.random7,
                R.drawable.random8,
                R.drawable.random9,
                R.drawable.random10,
                R.drawable.random11,
                R.drawable.random12,
                R.drawable.random13,
                R.drawable.random14,
                R.drawable.random15,R.drawable.random16,R.drawable.random17,R.drawable.random18,R.drawable.random19
    };

    public static int getRandomPic(){
        int i = new Random().nextInt(19);
        return pictures[i];
    }




}
