package com.unionyy.mobile.spdt.skin.core

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.AnyRes
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import com.unionyy.mobile.spdt.Spdt
import com.unionyy.mobile.spdt.skin.SkinResource

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class SpdtSkinManager(private val spdtCtx: SpdtSkinContext) : SkinResource {

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
            if (Build.VERSION.SDK_INT >= 23) {
                getColorStateList(id, context.theme)
            } else {
                getColorStateList(id)
            }
        }

    override fun getColor(resId: Int): Int = getColor(spdtCtx.app, resId)

    override fun getColor(context: Context, @ColorRes resId: Int): Int =
        swapResId(context, resId) { id ->
            ContextCompat.getColor(context, id)
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
                "hook resource: resName = ${hookRes.hookResName} " +
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

    fun getTargetResId(context: Context, resId: Int): ResId {
        return try {
            val resName = context.resources.getResourceEntryName(resId)
            val hookName = resName + Spdt.currentFlavor().resourceSuffix
            val type = context.resources.getResourceTypeName(resId)
            val identifier = context.resources.getIdentifier(hookName, type, context.packageName)
            ResId(identifier, resName, hookName, type)
        } catch (e: Exception) {
            // 换肤失败不至于应用崩溃.
            ResId(invalidID, "", "", "")
        }
    }

    data class ResId(
        @AnyRes val resId: Int,
        val resName: String,
        val hookResName: String,
        val resType: String
    )
}