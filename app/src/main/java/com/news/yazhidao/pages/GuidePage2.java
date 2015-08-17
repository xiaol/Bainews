package com.news.yazhidao.pages;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.news.yazhidao.R;

public class GuidePage2 extends Fragment {

    private View rootView;
    private Button btn_experence;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fgt_guidepage2, container, false);
        btn_experence = (Button) rootView.findViewById(R.id.btnexperence);
        btn_experence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getActivity().getSharedPreferences("showflag", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("isshow",true);
                editor.commit();

                Intent intent = new Intent(getActivity(), HomeAty.class);
                startActivity(intent);

                getActivity().finish();
            }
        });
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {

    }

    private void findViews() {
    }


}
