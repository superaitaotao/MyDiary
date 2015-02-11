package com.here.superaitaotaotv.mydiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;

/**
 * Created by superaitaotaoTV on 11/10/14.
 */
public class Password extends Fragment{

    GridView gridView;
    ImageView imageView1,imageView2,imageView3,imageView4;
    TextView passwordTextView;

    private String password,storedPassword;

    private int[] mPIds = {
            R.drawable.one, R.drawable.two,
            R.drawable.three, R.drawable.four,
            R.drawable.five, R.drawable.six,
            R.drawable.seven, R.drawable.eight,
            R.drawable.nine
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.password_fragment, null);
        gridView = (GridView)v.findViewById(R.id.grid_view);
        imageView1 = (ImageView)v.findViewById(R.id.image1);
        imageView2 = (ImageView)v.findViewById(R.id.image2);
        imageView3 = (ImageView)v.findViewById(R.id.image3);
        imageView4 = (ImageView)v.findViewById(R.id.image4);
        passwordTextView = (TextView)v.findViewById(R.id.password_text_view);
        passwordTextView.setText(R.string.password_enter_pass_word_text);
        password = "";
        storedPassword = getActivity().getSharedPreferences("Login",0).getString("password","");
        if(storedPassword.equals("") ){
            Log.d("Password", "gotProblem");
        }
        gridView.setAdapter(new GridAdapter(this.getActivity().getApplicationContext()));
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (password.length()){
                    case 0:
                        imageView1.setImageResource(mPIds[i]);
                        break;
                    case 1:
                        imageView2.setImageResource(mPIds[i]);
                        break;
                    case 2:
                        imageView3.setImageResource(mPIds[i]);
                        break;
                    case 3:
                        imageView4.setImageResource(mPIds[i]);
                        break;
                }
                password += i+1;
                if (password.length()==4){
                    if(password.equals(storedPassword)){
                        Toast.makeText(getActivity(),"Correct Password", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),DiaryList.class);
                        getActivity().startActivity(intent);
                    }else{
                        password = "";
                        imageView1.setImageBitmap(null);
                        imageView2.setImageBitmap(null);
                        imageView3.setImageBitmap(null);
                        imageView4.setImageBitmap(null);
                        Toast.makeText(getActivity(),R.string.password_wrong_password, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return v;
    }

    private class GridAdapter extends BaseAdapter{

        Context mContext;

        public  GridAdapter(Context context){
            mContext = context;
        }
        @Override
        public int getCount() {
            return mPIds.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ImageView imageView;
            if (view == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(120,120));
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setPadding(3, 3, 3, 3);
            } else {
                imageView = (ImageView) view;
            }
            imageView.setImageResource(mPIds[i]);
            return imageView;
        }
    }

}
