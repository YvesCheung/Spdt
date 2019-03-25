package com.unionyy.mobile.spdt.skin.widget.attrs

import android.content.res.TypedArray
import android.support.annotation.AttrRes
import android.support.annotation.StyleableRes
import android.util.AttributeSet
import android.view.View

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
abstract class AttributeHelper(
    protected val view: View,
    protected val attrs: AttributeSet?,
    @AttrRes protected val defStyleAttr: Int
) {

    companion object {
        const val INVALID_ID = 0
    }

    fun loadAttributes() {
        if (attrs != null) {
            onLoadAttributes()
        }
    }

    protected abstract fun onLoadAttributes()

    protected inline fun loadAttributes(
        @StyleableRes styleRes: IntArray,
        resourceGetter: TypedArray.() -> Unit
    ) {
        val attr = attrs ?: return

        val a = view.context.obtainStyledAttributes(
            attr, styleRes, defStyleAttr, 0)
        try {
            resourceGetter(a)
        } catch (e: Exception) {
            a.recycle()
        }
    }
}