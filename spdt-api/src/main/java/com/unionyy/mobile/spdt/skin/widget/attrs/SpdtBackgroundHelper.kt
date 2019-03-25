package com.unionyy.mobile.spdt.skin.widget.attrs

import android.support.annotation.DrawableRes
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.api.R

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtBackgroundHelper(
    view: View,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : AttributeHelper(view, attrs, defStyleAttr) {

    @DrawableRes
    private var mBackground: Int = INVALID_ID

    override fun onLoadAttributes() {
        loadAttributes(R.styleable.SpdtBackgroundHelper) {
            mBackground = getResourceId(R.styleable.SpdtBackgroundHelper_android_background, INVALID_ID)
        }
    }

    fun onSetBackgroundResource(background: Int) {
        mBackground = background
        applySkin()
    }

    override fun applySkin() {
        if (mBackground != INVALID_ID) {
            val drawable = Spdt.getDrawable(view.context, mBackground)
            ViewCompat.setBackground(view, drawable)
        }
    }
}