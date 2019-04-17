package com.unionyy.mobile.spdt.factory

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.unionyy.mobile.spdt.skin.factory.DefaultSkinFactory
import com.unionyy.mobile.spdt.skin.factory.SpdtSkinFactory
import com.unionyy.mobile.spdt.widget.SpdtSupportButton
import com.unionyy.mobile.spdt.widget.SpdtSupportEditText
import com.unionyy.mobile.spdt.widget.SpdtSupportImageView
import com.unionyy.mobile.spdt.widget.SpdtSupportTextView

/**
 * Created BY PYF 2019/4/16
 * email: pengyangfan@yy.com
 * 还没有写add last代码的地方
 */
class SupportSkinFactory : SpdtSkinFactory {

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        return when (name) {
            "AppCompatTextView" -> SpdtSupportTextView(context, attrs)
            "AppCompatButton" -> SpdtSupportButton(context, attrs)
            "AppCompatEditText" -> SpdtSupportEditText(context, attrs)
            "AppCompatImageView" -> SpdtSupportImageView(context, attrs)
            else -> null
        }
    }
}