package com.unionyy.mobile.spdt.skin.core

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.annotation.AnyRes
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

    override fun getDrawable(resId: Int): Drawable = getDrawable(spdtCtx.app, resId)

    override fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable =
        swapResId(context, resId) { id ->
            getDrawable(id)
        }

    override fun getColorStateList(resId: Int): ColorStateList =
        getColorStateList(spdtCtx.app, resId)

    override fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList =
        swapResId(context, resId) { id ->
            getColorStateList(id)
        }

    override fun getColor(resId: Int): Int = getColor(spdtCtx.app, resId)

    override fun getColor(context: Context, @ColorRes resId: Int): Int =
        swapResId(context, resId) { id ->
            getColor(id)
        }

    override fun getString(resId: Int): String = getString(spdtCtx.app, resId)

    override fun getString(context: Context, resId: Int): String =
        swapResId(context, resId) { id ->
            getString(id)
        }

    private inline fun <T : Any> swapResId(context: Context, resId: Int, getResEntry: Resources.(resId: Int) -> T): T {
        val hookRes = getTargetResId(context, resId)
        if (hookRes.resId != invalidID) {
            val hookResEntry = getResEntry(context.resources, hookRes.resId)
            spdtCtx.logger.debug("SpdtSkin",
                "hook resource: resName = ${hookRes.resName} " +
                    "type = ${hookRes.resType} " +
                    "resId = ${hookRes.resId} " +
                    "entry = ${toHexIfNumber(hookResEntry)}")
            return hookResEntry
        } else {
            val originResEntry = getResEntry(context.resources, resId)
            spdtCtx.logger.debug("SpdtSkin",
                "origin resource: resName = ${hookRes.resName} " +
                    "type = ${hookRes.resType} " +
                    "resId = $resId " +
                    "entry = ${toHexIfNumber(originResEntry)}")
            return originResEntry
        }
    }

    private fun <T : Any> toHexIfNumber(any: T): String =
        if (any is Int) {
            Integer.toHexString(any)
        } else {
            any.toString()
        }

    private fun getTargetResId(context: Context, resId: Int): ResId {
        return try {
            val resName = context.resources.getResourceEntryName(resId) +
                Spdt.currentFlavor().resourceSuffix
            val type = context.resources.getResourceTypeName(resId)
            val identifier = context.resources.getIdentifier(resName, type, context.packageName)
            ResId(resName, type, identifier)
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            ResId("", "", invalidID)
        }
    }

    private data class ResId(val resName: String, val resType: String, @AnyRes val resId: Int)
}