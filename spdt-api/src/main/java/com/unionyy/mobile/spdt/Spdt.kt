package com.unionyy.mobile.spdt

import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import android.util.LruCache
import com.unionyy.mobile.spdt.annotation.SpdtFlavor
import com.unionyy.mobile.spdt.skin.SkinClient
import com.unionyy.mobile.spdt.skin.core.SpdtSkinClient

object Spdt : SkinClient by SpdtSkinClient() {

    @JvmStatic
    fun inject(obj: Any) {
        val injectCls = "${obj.javaClass.name}\$\$SpdtInjector"
        try {
            Class.forName(injectCls)
                .getMethod("inject", obj.javaClass)
                .invoke(null, obj)
        } catch (e: Exception) {
            throw SpdtException("Could not Spdt#inject the object '$obj' " +
                "because '$injectCls' is invalid.", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <Spdt : Any> of(spdtCls: Class<Spdt>): Spdt {
        val clsName = "${spdtCls.name}\$\$SpdtFactory"
        return try {
            val factory: SpdtExpectToActualFactory<Spdt> = getOrNew(clsName) {
                Class.forName(clsName).newInstance() as SpdtExpectToActualFactory<Spdt>
            } ?: throw SpdtException("The method getOrNew('$clsName') return null.")

            factory.create()
                ?: throw SpdtException("The create() method in '$clsName' return null.")
        } catch (error: Exception) {
            throw SpdtException("Could not initialize the Spdt class '$spdtCls'", error)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <Spdt : Any> ofOrNull(spdtCls: Class<Spdt>): Spdt? {
        val clsName = "${spdtCls.name}\$\$SpdtFactory"
        return try {
            val factory: SpdtExpectToActualFactory<Spdt>? = getOrNew(clsName) {
                Class.forName(clsName).newInstance() as SpdtExpectToActualFactory<Spdt>
            }

            factory?.create()
        } catch (ignore: Exception) {
            null
        }
    }

    private val current by lazy { of(SpdtFlavor::class.java) }

    @JvmStatic
    fun currentFlavor(): SpdtFlavor = current

    private val cache = LruCache<String, SpdtExpectToActualFactory<*>>(100)

    @Suppress("UNCHECKED_CAST")
    private inline fun <Spdt : Any> getOrNew(
        clsName: String,
        newInstance: () -> SpdtExpectToActualFactory<Spdt>?
    ): SpdtExpectToActualFactory<Spdt>? {
        val factory: SpdtExpectToActualFactory<*>? = cache[clsName]
        return if (factory == null) {
            newInstance().also {
                cache.put(clsName, it)
            }
        } else {
            factory as SpdtExpectToActualFactory<Spdt>
        }
    }

    @JvmStatic
    fun drawable(@DrawableRes resId: Int): Drawable = getDrawable(resId)

    @JvmStatic
    fun color(@ColorRes resId: Int): Int = getColor(resId)

    @JvmStatic
    fun string(@StringRes resId: Int, vararg arg: Any): String {
        return if (arg.isEmpty()) {
            getString(resId)
        } else {
            String.format(getString(resId), *arg)
        }
    }
}