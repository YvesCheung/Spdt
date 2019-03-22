package com.unionyy.mobile.spdt.skin.factory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.unionyy.mobile.spdt.skin.widget.SpdtTextView

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class DefaultSkinFactory : SpdtSkinFactory {

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        return when (name) {
            "TextView" -> SpdtTextView(context, attrs)
            else -> null
        }
    }
}