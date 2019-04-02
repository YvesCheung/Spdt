package com.unionyy.mobile.spdt.skin.widget

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.Button
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtBackgroundHelper
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtTextAttrsHelper

//代码和textview 一模一样 能否想什么办法从那边继承过来
open class SpdtButton : Button {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
        super(context, attrs, defStyleAttr, defStyleRes) {
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