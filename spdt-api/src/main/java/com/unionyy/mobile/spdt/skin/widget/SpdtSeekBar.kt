package com.unionyy.mobile.spdt.skin.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtProgerssBarHelper
import com.unionyy.mobile.spdt.skin.widget.attrs.SpdtSeekBarHelper

/**
 * Created by 张宇 on 2019-11-21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class SpdtSeekBar : SeekBar {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private lateinit var spdtProgressBarHelper: SpdtProgerssBarHelper

    private lateinit var spdtSeekBarHelper: SpdtSeekBarHelper

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        spdtProgressBarHelper = SpdtProgerssBarHelper(this, attrs, defStyleAttr)
        spdtProgressBarHelper.loadAttributes()

        spdtSeekBarHelper = SpdtSeekBarHelper(this, attrs, defStyleAttr)
        spdtSeekBarHelper.loadAttributes()
    }
}