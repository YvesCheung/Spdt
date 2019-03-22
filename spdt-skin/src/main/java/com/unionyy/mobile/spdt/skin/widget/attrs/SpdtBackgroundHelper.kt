package com.unionyy.mobile.spdt.skin.widget.attrs

import android.util.AttributeSet
import android.view.View
import com.unionyy.mobile.spdt.skin.R

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtBackgroundHelper(
    view: View,
    attrs: AttributeSet,
    defStyleAttr: Int
) : AttributeHelper(view, attrs, defStyleAttr) {

    override fun loadAttributes() {
        loadAttributes(R.styleable.SpdtBackgroundHelper) {
            getResourceId(R.styleable.SpdtBackgroundHelper_android_background, INVALID_ID)
        }
    }
}