package com.unionyy.mobile.spdt.skin.widget.attrs

import android.util.AttributeSet
import android.widget.ProgressBar
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.api.R

class SpdtProgerssBarHelper(view: ProgressBar,
                            attrs: AttributeSet?,
                            defStyleAttr: Int
) : AttributeHelper(view, attrs, defStyleAttr) {

    private var mIndeterminateDrawable: Int = INVALID_ID
    private var mProgressDrawable: Int = INVALID_ID

    override fun onLoadAttributes() {
        loadAttributes(R.styleable.SpdtProgressBar) {
            mIndeterminateDrawable = getResourceId(R.styleable.SpdtProgressBar_android_indeterminateDrawable, INVALID_ID)
            mProgressDrawable = getResourceId(R.styleable.SpdtProgressBar_android_progressDrawable, INVALID_ID)
        }
    }

    override fun applySkin() {
        if (mIndeterminateDrawable != INVALID_ID) {
            val drawable = Spdt.getDrawable(view.context, mIndeterminateDrawable)
            drawable.bounds = (view as ProgressBar).indeterminateDrawable.bounds
            view.indeterminateDrawable = drawable
        }
        if (mProgressDrawable != INVALID_ID) {
            val drawable = Spdt.getDrawable(view.context, mProgressDrawable)
            (view as ProgressBar).progressDrawable = drawable
        }
    }
}