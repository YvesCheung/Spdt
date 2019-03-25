package com.unionyy.mobile.spdt.skin.widget

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.TextView
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtBackgroundHelper

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class SpdtTextView : TextView {

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

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        backgroundHelper = SpdtBackgroundHelper(this, attrs, defStyleAttr)
        backgroundHelper.loadAttributes()

    }

    override fun setBackgroundResource(resid: Int) {
        super.setBackgroundResource(resid)
        backgroundHelper.onSetBackgroundResource(resid)
    }
}