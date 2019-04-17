package com.unionyy.mobile.spdt.widget

import android.content.Context
import android.support.v7.widget.AppCompatEditText
import android.util.AttributeSet
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtBackgroundHelper
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtTextAttrsHelper

/**
 * Created BY PYF 2019/4/17
 * email: pengyangfan@yy.com
 */
class SpdtSupportEditText : AppCompatEditText {
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private lateinit var backgroundHelper: SpdtBackgroundHelper
    private lateinit var textAttrsHelper: SpdtTextAttrsHelper

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        backgroundHelper = SpdtBackgroundHelper(this, attrs, defStyleAttr)
        textAttrsHelper = SpdtTextAttrsHelper(this, attrs, defStyleAttr)
        backgroundHelper.loadAttributes()
        textAttrsHelper.loadAttributes()
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        backgroundHelper.onSetBackgroundResource(resid)
    }

    override fun setTextAppearance(resId: Int) {
        setTextAppearance(context, resId)
    }

    override fun setTextAppearance(context: Context?, resId: Int) {
        super.setTextAppearance(context, resId)
        textAttrsHelper.onSetTextAttrsResource(resId)
    }

    override fun setCompoundDrawablesRelativeWithIntrinsicBounds(start: Int, top: Int, end: Int, bottom: Int) {
        super.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)
        textAttrsHelper.onSetCompundDrawableWithInstrinsicBounds(start, top, end, bottom)
    }

    override fun setCompoundDrawablesWithIntrinsicBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)
        textAttrsHelper.onSetCompundDrawableWithInstrinsicBounds(left, top, right, bottom)
    }
}