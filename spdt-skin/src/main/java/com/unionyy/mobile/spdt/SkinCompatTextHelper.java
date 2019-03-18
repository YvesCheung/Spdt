package com.unionyy.mobile.spdt;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import skin.support.content.res.SkinCompatResources;
import skin.support.widget.SkinCompatHelper;

/**
 * Created by ximsfei on 2017/1/10.
 */

public class SkinCompatTextHelper extends SkinCompatHelper {
    private static final String TAG = "SkinCompatTextHelper";

    public static SkinCompatTextHelper create(TextView textView) {
        if (Build.VERSION.SDK_INT >= 17) {
            return new SkinCompatTextHelperV17(textView);
        }
        return new SkinCompatTextHelper(textView);
    }

    final TextView mView;

    private int mTextColorResId = INVALID_ID;
    private int mTextColorHintResId = INVALID_ID;
    protected int mDrawableBottomResId = INVALID_ID;
    protected int mDrawableLeftResId = INVALID_ID;
    protected int mDrawableRightResId = INVALID_ID;
    protected int mDrawableTopResId = INVALID_ID;

    public SkinCompatTextHelper(TextView view) {
        mView = view;
    }

    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        final Context context = mView.getContext();

        // First read the TextAppearance style id
        TypedArray a = context.obtainStyledAttributes(attrs, skin.support.R.styleable.SkinCompatTextHelper, defStyleAttr, 0);
        final int ap = a.getResourceId(skin.support.R.styleable.SkinCompatTextHelper_android_textAppearance, INVALID_ID);

        if (a.hasValue(skin.support.R.styleable.SkinCompatTextHelper_android_drawableLeft)) {
            mDrawableLeftResId = a.getResourceId(skin.support.R.styleable.SkinCompatTextHelper_android_drawableLeft, INVALID_ID);
        }
        if (a.hasValue(skin.support.R.styleable.SkinCompatTextHelper_android_drawableTop)) {
            mDrawableTopResId = a.getResourceId(skin.support.R.styleable.SkinCompatTextHelper_android_drawableTop, INVALID_ID);
        }
        if (a.hasValue(skin.support.R.styleable.SkinCompatTextHelper_android_drawableRight)) {
            mDrawableRightResId = a.getResourceId(skin.support.R.styleable.SkinCompatTextHelper_android_drawableRight, INVALID_ID);
        }
        if (a.hasValue(skin.support.R.styleable.SkinCompatTextHelper_android_drawableBottom)) {
            mDrawableBottomResId = a.getResourceId(skin.support.R.styleable.SkinCompatTextHelper_android_drawableBottom, INVALID_ID);
        }
        a.recycle();

        if (ap != INVALID_ID) {
            a = context.obtainStyledAttributes(ap, skin.support.R.styleable.SkinTextAppearance);
            if (a.hasValue(skin.support.R.styleable.SkinTextAppearance_android_textColor)) {
                mTextColorResId = a.getResourceId(skin.support.R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
            }
            if (a.hasValue(skin.support.R.styleable.SkinTextAppearance_android_textColorHint)) {
                mTextColorHintResId = a.getResourceId(
                        skin.support.R.styleable.SkinTextAppearance_android_textColorHint, INVALID_ID);
            }
            a.recycle();
        }

        // Now read the style's values
        a = context.obtainStyledAttributes(attrs, skin.support.R.styleable.SkinTextAppearance, defStyleAttr, 0);
        if (a.hasValue(skin.support.R.styleable.SkinTextAppearance_android_textColor)) {
            mTextColorResId = a.getResourceId(skin.support.R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
        }
        if (a.hasValue(skin.support.R.styleable.SkinTextAppearance_android_textColorHint)) {
            mTextColorHintResId = a.getResourceId(
                    skin.support.R.styleable.SkinTextAppearance_android_textColorHint, INVALID_ID);
        }
        a.recycle();
        applySkin();
    }

    public void onSetTextAppearance(Context context, int resId) {
        final TypedArray a = context.obtainStyledAttributes(resId, skin.support.R.styleable.SkinTextAppearance);
        if (a.hasValue(skin.support.R.styleable.SkinTextAppearance_android_textColor)) {
            mTextColorResId = a.getResourceId(skin.support.R.styleable.SkinTextAppearance_android_textColor, INVALID_ID);
        }
        if (a.hasValue(skin.support.R.styleable.SkinTextAppearance_android_textColorHint)) {
            mTextColorHintResId = a.getResourceId(skin.support.R.styleable.SkinTextAppearance_android_textColorHint, INVALID_ID);
        }
        a.recycle();
        applyTextColorResource();
        applyTextColorHintResource();
    }

    private void applyTextColorHintResource() {
        mTextColorHintResId = checkResourceId(mTextColorHintResId);
        if (mTextColorHintResId != INVALID_ID) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                ColorStateList color = SkinCompatResources.getColorStateList(mView.getContext(), mTextColorHintResId);
                mView.setHintTextColor(color);
            } catch (Exception e) {
            }
        }
    }

    private void applyTextColorResource() {
        mTextColorResId = checkResourceId(mTextColorResId);
        if (mTextColorResId != INVALID_ID) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                ColorStateList color = SkinCompatResources.getColorStateList(mView.getContext(), mTextColorResId);
                mView.setTextColor(color);
            } catch (Exception e) {
            }
        }
    }

    public void onSetCompoundDrawablesRelativeWithIntrinsicBounds(
            int start, int top, int end, int bottom) {
        mDrawableLeftResId = start;
        mDrawableTopResId = top;
        mDrawableRightResId = end;
        mDrawableBottomResId = bottom;
        applyCompoundDrawablesRelativeResource();
    }

    public void onSetCompoundDrawablesWithIntrinsicBounds(
            int left, int top, int right, int bottom) {
        mDrawableLeftResId = left;
        mDrawableTopResId = top;
        mDrawableRightResId = right;
        mDrawableBottomResId = bottom;
        applyCompoundDrawablesResource();
    }

    protected void applyCompoundDrawablesRelativeResource() {
        applyCompoundDrawablesResource();
    }

    protected void applyCompoundDrawablesResource() {
        Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;
        mDrawableLeftResId = checkResourceId(mDrawableLeftResId);
        if (mDrawableLeftResId != INVALID_ID) {
            drawableLeft = SkinCompatResources.getDrawable(mView.getContext(), mDrawableLeftResId);
        }
        mDrawableTopResId = checkResourceId(mDrawableTopResId);
        if (mDrawableTopResId != INVALID_ID) {
            drawableTop = SkinCompatResources.getDrawable(mView.getContext(), mDrawableTopResId);
        }
        mDrawableRightResId = checkResourceId(mDrawableRightResId);
        if (mDrawableRightResId != INVALID_ID) {
            drawableRight = SkinCompatResources.getDrawable(mView.getContext(), mDrawableRightResId);
        }
        mDrawableBottomResId = checkResourceId(mDrawableBottomResId);
        if (mDrawableBottomResId != INVALID_ID) {
            drawableBottom = SkinCompatResources.getDrawable(mView.getContext(), mDrawableBottomResId);
        }
        if (mDrawableLeftResId != INVALID_ID
                || mDrawableTopResId != INVALID_ID
                || mDrawableRightResId != INVALID_ID
                || mDrawableBottomResId != INVALID_ID) {
            mView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }
    }

    public int getTextColorResId() {
        return mTextColorResId;
    }

    @Override
    public void applySkin() {
        applyCompoundDrawablesRelativeResource();
        applyTextColorResource();
        applyTextColorHintResource();
    }
}
