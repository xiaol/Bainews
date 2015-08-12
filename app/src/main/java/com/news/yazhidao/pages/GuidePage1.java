package com.news.yazhidao.pages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.news.yazhidao.R;

public class GuidePage1 extends Fragment {

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("jigang", "-----CategoryFgt  onCreateView");

        rootView = inflater.inflate(R.layout.fgt_guidepage, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {

    }

    private void findViews() {
    }


}
