package com.unionyy.mobile.spdt.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtProgerssBarHelper

class SpdtProgressBar : ProgressBar {


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private lateinit var spdtProgressBarHelper: SpdtProgerssBarHelper

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        spdtProgressBarHelper = SpdtProgerssBarHelper(this, attrs, defStyleAttr)
        spdtProgressBarHelper.loadAttributes()
    }
}