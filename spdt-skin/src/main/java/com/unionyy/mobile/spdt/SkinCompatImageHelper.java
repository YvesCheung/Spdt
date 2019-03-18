package com.unionyy.mobile.spdt;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatHelper;

/**
 * Created by ximsfei on 2017/1/12.
 */
public class SkinCompatImageHelper extends SkinCompatHelper {
    private static final String TAG = SkinCompatImageHelper.class.getSimpleName();
    private final ImageView mView;
    private int mSrcResId = INVALID_ID;
    private int mSrcCompatResId = INVALID_ID;

    public SkinCompatImageHelper(ImageView imageView) {
        mView = imageView;
    }

    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = null;
        try {
            a = mView.getContext().obtainStyledAttributes(attrs, skin.support.R.styleable.SkinCompatImageView, defStyleAttr, 0);
            mSrcResId = a.getResourceId(skin.support.R.styleable.SkinCompatImageView_android_src, INVALID_ID);
            mSrcCompatResId = a.getResourceId(skin.support.R.styleable.SkinCompatImageView_srcCompat, INVALID_ID);
        } finally {
            if (a != null) {
                a.recycle();
            }
        }
        applySkin();
    }

    public void setImageResource(int resId) {
        mSrcResId = resId;
        applySkin();
    }

    @Override
    public void applySkin() {
        mSrcCompatResId = checkResourceId(mSrcCompatResId);
        if (mSrcCompatResId != INVALID_ID) {
            Drawable drawable = SkinCompatResources.getDrawable(mView.getContext(), mSrcCompatResId);
            if (drawable != null) {
                mView.setImageDrawable(drawable);
            }
        } else {
            mSrcResId = checkResourceId(mSrcResId);
            if (mSrcResId == INVALID_ID) {
                return;
            }
            Drawable drawable = SkinCompatResources.getDrawable(mView.getContext(), mSrcResId);
            if (drawable != null) {
                mView.setImageDrawable(drawable);
            }
        }
    }
}
