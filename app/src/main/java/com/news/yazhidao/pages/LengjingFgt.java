package com.news.yazhidao.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.news.yazhidao.R;


public class LengjingFgt extends Fragment {

    private View rootView;
    private LinearLayout ll_root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category_lengjing, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void findViews() {
        ll_root = (LinearLayout) rootView.findViewById(R.id.ll_rootview);
    }

}
