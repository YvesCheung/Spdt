package com.unionyy.mobile.spdt.skin

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes

/**
 * Created by 张宇 on 2019/3/25.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface SkinResource {

    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable

    fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList

    fun getColor(context: Context, @ColorRes resId: Int): Int
}