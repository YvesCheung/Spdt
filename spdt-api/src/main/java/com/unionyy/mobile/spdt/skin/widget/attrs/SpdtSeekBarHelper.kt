package com.unionyy.mobile.spdt.skin.widget.attrs

import android.os.Build
import android.util.AttributeSet
import android.widget.AbsSeekBar
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.api.R

/**
 * Created by 张宇 on 2019-11-21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtSeekBarHelper(
    private val seekBar: AbsSeekBar,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : AttributeHelper(seekBar, attrs, defStyleAttr) {

    private var mThumbDrawable: Int = INVALID_ID

    override fun onLoadAttributes() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            loadAttributes(R.styleable.SpdtSeekBar) {
                mThumbDrawable = getResourceId(R.styleable.SpdtSeekBar_android_thumb, INVALID_ID)
            }
        }
    }

    override fun applySkin() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (mThumbDrawable != INVALID_ID) {
                val drawable = Spdt.getDrawable(view.context, mThumbDrawable)
                drawable.bounds = seekBar.thumb.bounds
                seekBar.thumb = drawable
            }
        }
    }
}