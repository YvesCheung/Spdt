package com.unionyy.mobile.spdt.skin.core

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.skin.SkinResource

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtSkinManager(private val spdtCtx: SpdtSkinContext) : SkinResource {

    private val invalidID = 0

    init {
        SkinLifecycle.install(spdtCtx)
    }

    override fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable {

        val hookResId = getTargetResId(context, resId)
        if (hookResId != invalidID) {
            return context.resources.getDrawable(hookResId)
        }

        return context.resources.getDrawable(resId)
    }

    override fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList {
        val hookResId = getTargetResId(context, resId)
        if (hookResId != invalidID) {
            return context.resources.getColorStateList(hookResId)
        }

        return context.resources.getColorStateList(resId)
    }

    override fun getColor(context: Context, @ColorRes resId: Int): Int {
        val hookResId = getTargetResId(context, resId)
        if (hookResId != invalidID) {
            return context.resources.getColor(hookResId)
        }

        return context.resources.getColor(resId)
    }

    override fun getString(context: Context, resId: Int): String {
        val hookResId = getTargetResId(context, resId)
        if (hookResId != invalidID) {
            return context.resources.getString(hookResId)
        }

        return context.resources.getString(resId)
    }

    private fun getTargetResId(context: Context, resId: Int): Int {
        return try {
            val resName = context.resources.getResourceEntryName(resId) +
                Spdt.currentFlavor().resourceSuffix
            val type = context.resources.getResourceTypeName(resId)
            val identifier = context.resources.getIdentifier(resName, type, context.packageName)
            spdtCtx.logger.debug("SpdtSkin",
                "resName = $resName " +
                    "type = $type " +
                    "resId = $identifier")
            identifier
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            invalidID
        }
    }
}