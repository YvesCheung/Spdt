package com.unionyy.mobile.spdt

import android.content.Context
import android.util.AttributeSet
import android.view.View
import skin.support.app.SkinLayoutInflater

/**
 * Created by 张宇 on 2019/3/7.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class SpdtInflater : SkinLayoutInflater {

    override fun createView(context: Context, name: String?, attrs: AttributeSet): View? {
        return when (name) {
            "View" -> SkinCompatView(context, attrs)
            "LinearLayout" -> SkinCompatLinearLayout(context, attrs)
            "RelativeLayout" -> SkinCompatRelativeLayout(context, attrs)
            "FrameLayout" -> SkinCompatFrameLayout(context, attrs)
            "TextView" -> SkinCompatTextView(context, attrs)
            "ImageView" -> SkinCompatImageView(context, attrs)
            "Button" -> SkinCompatButton(context, attrs)
            "EditText" -> SkinCompatEditText(context, attrs)
            else -> null
        }
    }

}