package com.news.yazhidao.pages;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

import com.news.yazhidao.R;
import com.news.yazhidao.entity.Channel;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;


public class LengjingFgt extends Fragment {

    private View rootView;
    private GridView mgvCategory;
    private ArrayList<Channel> marrCategoryName;
    private TypedArray marrCategoryDrawable;
    private FloatingActionButton leftCenterButton;
    private FloatingActionButton.LayoutParams starParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fgt_category, container, false);
        initVars();
        findViews();
        return rootView;
    }

    private void initVars() {

    }

    @Override
    public void onStop() {

        if (leftCenterButton != null) {
            leftCenterButton.setVisibility(View.GONE);
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        if (leftCenterButton != null) {
            leftCenterButton.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }

    private void findViews() {
        mgvCategory = (GridView) rootView.findViewById(R.id.category_gridview);

        // Set up the large red button on the center right side
        // With custom button and content sizes and margins
        int redActionButtonSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_size);
        int imgSize = getResources().getDimensionPixelSize(R.dimen.img_size);
        int redActionButtonMargin = getResources().getDimensionPixelOffset(R.dimen.action_button_margin);
        int redActionButtonContentSize = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_size);
        int redActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.red_action_button_content_margin);
        int redActionMenuRadius = getResources().getDimensionPixelSize(R.dimen.red_action_menu_radius);
        int blueSubActionButtonSize = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_size);
        int blueSubActionButtonContentMargin = getResources().getDimensionPixelSize(R.dimen.blue_sub_action_button_content_margin);

        final ImageView fabIconStar = new ImageView(getActivity());
        fabIconStar.setImageDrawable(getResources().getDrawable(R.drawable.icon_lengjing_digger));

        starParams = new FloatingActionButton.LayoutParams(redActionButtonSize, redActionButtonSize);
        starParams.setMargins(redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin,
                redActionButtonMargin);
        fabIconStar.setLayoutParams(starParams);

        FloatingActionButton.LayoutParams fabIconStarParams = new FloatingActionButton.LayoutParams(redActionButtonContentSize, redActionButtonContentSize);
        fabIconStarParams.setMargins(redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin,
                redActionButtonContentMargin);

        leftCenterButton = new FloatingActionButton.Builder(getActivity())
                .setContentView(fabIconStar, fabIconStarParams)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .setLayoutParams(starParams)
                .build();

        // Set up customized SubActionButtons for the right center menu
        SubActionButton.Builder lCSubBuilder = new SubActionButton.Builder(getActivity());

        int buttonContentSize = getResources().getDimensionPixelSize(R.dimen.sub_action_button_content_size);

        FrameLayout.LayoutParams blueContentParams = new FrameLayout.LayoutParams(buttonContentSize, buttonContentSize);
        blueContentParams.setMargins(blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin,
                blueSubActionButtonContentMargin);
        lCSubBuilder.setLayoutParams(blueContentParams);
        // Set custom layout params
        FrameLayout.LayoutParams blueParams = new FrameLayout.LayoutParams(blueSubActionButtonSize, blueSubActionButtonSize);
        lCSubBuilder.setLayoutParams(blueParams);


        ImageView lcIcon1 = new ImageView(getActivity());
        ImageView lcIcon2 = new ImageView(getActivity());
        ImageView lcIcon3 = new ImageView(getActivity());


        lcIcon1.setImageResource(R.drawable.icon_lengjing_text);
        lcIcon2.setImageResource(R.drawable.icon_lengjing_url);
        lcIcon3.setImageResource(R.drawable.icon_lengjing_base);

        lcIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // Build another menu with custom options
        final FloatingActionMenu leftCenterMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon1, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon2, blueContentParams).build())
                .addSubActionView(lCSubBuilder.setContentView(lcIcon3, blueContentParams).build())
                .setRadius(redActionMenuRadius)
                .setStartAngle(-40)
                .setEndAngle(-140)
                .attachTo(leftCenterButton)
                .build();

        leftCenterMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                fabIconStar.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 135);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                fabIconStar.setRotation(135);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconStar, pvhR);
                animation.start();
            }
        });

    }
}
