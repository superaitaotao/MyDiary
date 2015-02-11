package com.here.superaitaotaotv.mydiary;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.superaitaotaotv.mydiary.R;

/**
 * Created by superaitaotaoTV on 12/10/14.
 */
public class SetPassword extends Fragment {

    GridView gridView;
    ImageView imageView1, imageView2, imageView3, imageView4;
    TextView textView;
    LinearLayout linearLayout;
    Button clearButton, cancelButton, setButton;

    private String password, storedPassword;
    private Boolean hasOldPassword, oldPasswordCorrect;

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
        gridView = (GridView) v.findViewById(R.id.grid_view);
        imageView1 = (ImageView) v.findViewById(R.id.image1);
        imageView2 = (ImageView) v.findViewById(R.id.image2);
        imageView3 = (ImageView) v.findViewById(R.id.image3);
        imageView4 = (ImageView) v.findViewById(R.id.image4);
        textView = (TextView)v.findViewById(R.id.password_text_view);
        linearLayout = (LinearLayout)v.findViewById(R.id.buttom_linear_layout);

        oldPasswordCorrect = false;
        hasOldPassword = false;

        cancelButton = new Button(getActivity());
        cancelButton.setText(R.string.set_password_cancel_button_text);
        cancelButton.setTextColor(Color.parseColor("#ffffa600"));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        linearLayout.addView(cancelButton);

        clearButton = new Button(getActivity());
        clearButton.setText(R.string.set_password_clear_button_text);
        clearButton.setTextColor(Color.parseColor("#ffffa600"));
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        setButton = new Button(getActivity());
        setButton.setText(R.string.set_password_done_button_text);
        setButton.setTextColor(Color.parseColor("#ffffa600"));
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.length()<4) {
                    Toast.makeText(getActivity(),R.string.set_password_enter_4_digits_text, Toast.LENGTH_SHORT).show();
                }
                if ( (password.length() == 4)&(oldPasswordCorrect)){
                    saveNewPassword();
                    Toast.makeText(getActivity(),password,Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(getActivity(),DiaryList.class);
                    startActivity(i);
                }
            }
        });

        storedPassword = getActivity().getSharedPreferences("Login",0).getString("password",null);

        if ( storedPassword != null) {
            textView.setText(R.string.set_password_enter_old_password_text);
            hasOldPassword = true;
            oldPasswordCorrect = false;
        } else {
            textView.setText(R.string.set_password_enter_new_password_text);
            oldPasswordCorrect = true;
            linearLayout.addView(clearButton);
            linearLayout.addView(setButton);
        }

        password = "";

        gridView.setAdapter(new GridAdapter(this.getActivity().getApplicationContext()));
        gridView.setNumColumns(3);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (password.length()) {
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
                password += i + 1;
                if (password.length() == 4) {
                    if((hasOldPassword)&(!oldPasswordCorrect)){
                        if( (password.equals(storedPassword))) {
                            Toast.makeText(getActivity(), "Correct Password", Toast.LENGTH_SHORT).show();
                            oldPasswordCorrect = true;
                            textView.setText(R.string.set_password_enter_new_password_text);
                            linearLayout.addView(clearButton);
                            linearLayout.addView(setButton);
                            clear();
                        } else {
                            clear();
                            Toast.makeText(getActivity(), R.string.password_wrong_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

        return v;
    }

    private void saveNewPassword(){
        SharedPreferences pref = getActivity().getSharedPreferences("Login",0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("password",password);
        editor.commit();
    }

    private void clear(){
        password = "";
        imageView1.setImageBitmap(null);
        imageView2.setImageBitmap(null);
        imageView3.setImageBitmap(null);
        imageView4.setImageBitmap(null);
    }
    private class GridAdapter extends BaseAdapter {

        Context mContext;

        public GridAdapter(Context context) {
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
                imageView.setLayoutParams(new GridView.LayoutParams(120, 120));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(3, 3, 3, 3);
            } else {
                imageView = (ImageView) view;
            }
            imageView.setImageResource(mPIds[i]);
            return imageView;
        }
    }

}


