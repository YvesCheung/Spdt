package com.unionyy.mobile.spdt.skin.widget.attrs

import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.api.R

class SpdtImageHelper(view: ImageView,
                      attrs: AttributeSet?,
                      defStyleAttr: Int
) : AttributeHelper(view, attrs, defStyleAttr) {

    private var mSrc: Int = INVALID_ID

    override fun onLoadAttributes() {
        loadAttributes(R.styleable.SpdtImageView) {
            mSrc = getResourceId(R.styleable.SpdtImageView_android_src, INVALID_ID)
        }
    }

    fun setImageResources(resId: Int) {
        mSrc = resId
        applySkin()
    }

    override fun applySkin() {
        if (mSrc != INVALID_ID) {
            val drawable = Spdt.getDrawable(view.context, mSrc)
            (view as ImageView).setImageDrawable(drawable)
        }
    }
}