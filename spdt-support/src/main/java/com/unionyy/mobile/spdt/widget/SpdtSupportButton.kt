package com.unionyy.mobile.spdt.widget

import android.content.Context
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtBackgroundHelper
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtTextAttrsHelper

/**
 * Created BY PYF 2019/4/17
 * email: pengyangfan@yy.com
 * 代码长得好像啊。
 */
class SpdtSupportButton : AppCompatButton {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private lateinit var backgroundHelper: SpdtBackgroundHelper
    private lateinit var textAttrHelper: SpdtTextAttrsHelper

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        backgroundHelper = SpdtBackgroundHelper(this, attrs, defStyleAttr)
        backgroundHelper.loadAttributes()
        textAttrHelper = SpdtTextAttrsHelper(this, attrs, defStyleAttr)
        textAttrHelper.loadAttributes()
    }

    //其实没太去看为什么要选择这些方法复写
    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        backgroundHelper.onSetBackgroundResource(resid)
    }

    override fun setTextAppearance(resId: Int) {
        setTextAppearance(context, resId)
    }

    override fun setTextAppearance(context: Context?, resId: Int) {
        super.setTextAppearance(context, resId)
        textAttrHelper.onSetTextAttrsResource(resId)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(start: Int, top: Int, end: Int, bottom: Int) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        textAttrHelper.onSetCompundDrawableWithInstrinsicBounds(start, top, end, bottom)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        textAttrHelper.onSetCompundDrawableWithInstrinsicBounds(left, top, right, bottom)
    }
}