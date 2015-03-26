package com.news.yazhidao.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.news.yazhidao.R;

import java.util.Hashtable;

public class TextViewExtend extends TextView {
    private static final String TAG = "TextViewExtend";
    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();
    public TextViewExtend(Context context) {
        super(context);
    }

    public TextViewExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public TextViewExtend(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String customFont = a.getString(R.styleable.TextViewPlus_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {

        Typeface tf = get(asset,ctx);

        if(tf==null)
        {
            return false;
        }

        setTypeface(tf);
        return true;
    }

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromAsset(context.getAssets(), name);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}