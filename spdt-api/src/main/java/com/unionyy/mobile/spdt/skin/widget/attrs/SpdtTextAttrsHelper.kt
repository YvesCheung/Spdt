package com.unionyy.mobile.spdt.skin.widget.attrs

import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.api.R

class SpdtTextAttrsHelper(
    view: TextView,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : AttributeHelper(view, attrs, defStyleAttr) {

    //有多少个属性是有必要支持换肤的
    @ColorRes
    private var mTextColor: Int = INVALID_ID //全都拿id
    @ColorRes
    private var mTextColorHint: Int = INVALID_ID
    private var mDrawableLeft: Int = INVALID_ID
    private var mDrawableRight: Int = INVALID_ID
    private var mDrawableTop: Int = INVALID_ID
    private var mDrawaleBottom: Int = INVALID_ID

    //初始化时走这个
    override fun onLoadAttributes() {
        loadAttributes(R.styleable.SpdtTextAppearance) {
            mTextColor = getResourceId(R.styleable.SpdtTextAppearance_android_textColor, INVALID_ID)
            mTextColorHint = getResourceId(R.styleable.SpdtTextAppearance_android_textColorHint, INVALID_ID)
        }
        loadAttributes(R.styleable.SpdtTextAttrsHelper) {
            mDrawableLeft = getResourceId(R.styleable.SpdtTextAttrsHelper_android_drawableLeft, INVALID_ID)
            mDrawableRight = getResourceId(R.styleable.SpdtTextAttrsHelper_android_drawableRight, INVALID_ID)
            mDrawableTop = getResourceId(R.styleable.SpdtTextAttrsHelper_android_drawableTop, INVALID_ID)
            mDrawaleBottom = getResourceId(R.styleable.SpdtTextAttrsHelper_android_drawableBottom, INVALID_ID)
        }
    }

    //动态设置走这个
    fun onSetTextAttrsResource(resId: Int) {
        //设置了 default resId
        //没太看懂为什么这样可以取到正确的resId
        val type = view.context.obtainStyledAttributes(resId, R.styleable.SpdtTextAppearance)
        if (type.hasValue(R.styleable.SpdtTextAppearance_android_textColor)) {
            mTextColor = type.getResourceId(R.styleable.SpdtTextAppearance_android_textColor, INVALID_ID)
        }
        if (type.hasValue(R.styleable.SpdtTextAppearance_android_textColorHint)) {
            mTextColorHint = type.getResourceId(R.styleable.SpdtTextAppearance_android_textColorHint, INVALID_ID)
        }
        type?.recycle()
        applyTextColorResource()
        applyTextColorHintResource()
    }

    fun onSetCompundDrawableWithInstrinsicBounds(
        @DrawableRes left: Int,
        @DrawableRes top: Int,
        @DrawableRes right: Int,
        @DrawableRes bottom: Int
    ) {
        mDrawableLeft = left
        mDrawableRight = right
        mDrawableTop = top
        mDrawaleBottom = bottom
        applyCompoundDrawablesResource()
    }

    override fun applySkin() {
        applyTextColorResource()
        applyTextColorHintResource()
        applyCompoundDrawablesResource()
    }

    private fun applyTextColorResource() {
        if (mTextColor != INVALID_ID) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            // 我都不想理火腿肠了
            try {
                val color = Spdt.getColorStateList(view.context, mTextColor)
                (view as TextView).setTextColor(color)
            } catch (e: Exception) {
                Log.e("SpdtTextAttrsHelper", "e: ${e.message}")
            }
        }
    }

    private fun applyTextColorHintResource() {
        if (mTextColorHint != INVALID_ID) {
            // TODO: HTC_U-3u OS:8.0上调用framework的getColorStateList方法，有可能抛出异常，暂时没有找到更好的解决办法.
            // issue: https://github.com/ximsfei/Android-skin-support/issues/110
            try {
                val color = Spdt.getColorStateList(view.context, mTextColorHint)
                (view as TextView).setHintTextColor(color)
            } catch (e: Exception) {
            }
        }
    }

    private fun applyCompoundDrawablesResource() {
        var drawableLeft: Drawable? = null
        var drawableTop: Drawable? = null
        var drawableRight: Drawable? = null
        var drawableBottom: Drawable? = null
        if (mDrawableLeft != INVALID_ID) {
            drawableLeft = Spdt.getDrawable(view.context, mDrawableLeft)
        }
        if (mDrawableTop != INVALID_ID) {
            drawableTop = Spdt.getDrawable(view.context, mDrawableTop)
        }
        if (mDrawableRight != INVALID_ID) {
            drawableRight = Spdt.getDrawable(view.context, mDrawableRight)
        }
        if (mDrawaleBottom != INVALID_ID) {
            drawableBottom = Spdt.getDrawable(view.context, mDrawaleBottom)
        }
        if (mDrawableLeft != INVALID_ID
            || mDrawableRight != INVALID_ID
            || mDrawableTop != INVALID_ID
            || mDrawaleBottom != INVALID_ID) {
            (view as TextView).setCompoundDrawablesWithIntrinsicBounds(
                drawableLeft,
                drawableTop,
                drawableRight,
                drawableBottom
            )
        }
    }
}