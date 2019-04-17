package com.unionyy.mobile.spdt.skin.factory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.unionyy.mobile.spdt.skin.widget.*

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class DefaultSkinFactory : SpdtSkinFactory {

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        return when (name) {
            "TextView" -> SpdtTextView(context, attrs)
            "Button" -> SpdtButton(context, attrs)
            "EditText" -> SpdtEditTextView(context, attrs)
            "ImageView" -> SpdtImageView(context, attrs)
            "FrameLayout" -> SpdtFrameLayout(context, attrs)
            "LinearLayout" -> SpdtLinearLayout(context, attrs)
            "ProgressBar" -> SpdtProgressBar(context, attrs)
            "RelativeLayout" -> SpdtRelativeLayout(context, attrs)
            "ScrollView" -> SpdtScrollView(context, attrs)
            "View" -> SpdtView(context, attrs)
            else -> null
        }
    }
}