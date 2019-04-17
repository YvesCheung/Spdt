package com.unionyy.mobile.spdt.widget

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtBackgroundHelper

/**
 * Created BY PYF 2019/4/17
 * email: pengyangfan@yy.com
 */
class SpdtConstrainLayout : ConstraintLayout {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private lateinit var backgroundHelper: SpdtBackgroundHelper

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        backgroundHelper = SpdtBackgroundHelper(this, attrs, defStyleAttr)
        backgroundHelper.loadAttributes()
    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        backgroundHelper.onSetBackgroundResource(resid)
    }

}