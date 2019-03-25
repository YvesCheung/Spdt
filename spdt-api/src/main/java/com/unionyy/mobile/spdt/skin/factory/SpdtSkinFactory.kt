package com.unionyy.mobile.spdt.skin.factory

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface SpdtSkinFactory {

    fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View?
}