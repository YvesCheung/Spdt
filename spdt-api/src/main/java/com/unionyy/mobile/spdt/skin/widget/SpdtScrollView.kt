package com.unionyy.mobile.spdt.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtBackgroundHelper

open class SpdtScrollView : ScrollView {

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