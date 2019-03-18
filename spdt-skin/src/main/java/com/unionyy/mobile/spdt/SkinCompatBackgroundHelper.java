package com.unionyy.mobile.spdt;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatHelper;

/**
 * Created by ximsfei on 2017/1/10.
 */

public class SkinCompatBackgroundHelper extends SkinCompatHelper {
    private final View mView;

    private int mBackgroundResId = INVALID_ID;

    public SkinCompatBackgroundHelper(View view) {
        mView = view;
    }

    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mView.getContext().obtainStyledAttributes(attrs, skin.support.R.styleable.SkinBackgroundHelper, defStyleAttr, 0);
        try {
            if (a.hasValue(skin.support.R.styleable.SkinBackgroundHelper_android_background)) {
                mBackgroundResId = a.getResourceId(
                        skin.support.R.styleable.SkinBackgroundHelper_android_background, INVALID_ID);
            }
        } finally {
            a.recycle();
        }
        applySkin();
    }

    public void onSetBackgroundResource(int resId) {
        mBackgroundResId = resId;
        // Update the default background tint
        applySkin();
    }

    @Override
    public void applySkin() {
        mBackgroundResId = checkResourceId(mBackgroundResId);
        if (mBackgroundResId == INVALID_ID) {
            return;
        }
        Drawable drawable = SkinCompatResources.getDrawable(mView.getContext(), mBackgroundResId);
        if (drawable != null) {
            int paddingLeft = mView.getPaddingLeft();
            int paddingTop = mView.getPaddingTop();
            int paddingRight = mView.getPaddingRight();
            int paddingBottom = mView.getPaddingBottom();
            if (Build.VERSION.SDK_INT >= 16) {
                mView.setBackground(drawable);
            } else {
                mView.setBackgroundDrawable(drawable);
            }
            mView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        }
    }
}
