package com.unionyy.mobile.spdt

import android.util.LruCache
import com.unionyy.mobile.spdt.annotation.SpdtFlavor

object Spdt {

    @JvmStatic
    fun inject(obj: Any) {
        val injectCls = "${obj.javaClass.name}\$\$SpdtInjector"
        try {
            Class.forName(injectCls)
                .getMethod("inject", obj.javaClass)
                .invoke(null, obj)
        } catch (e: Exception) {
            throw RuntimeException("Could not Spdt#inject the object '$obj' " +
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
            } ?: throw RuntimeException("The method getOrNew('$clsName') return null.")

            factory.create()
                ?: throw RuntimeException("The create() method in '$clsName' return null.")
        } catch (error: Exception) {
            throw RuntimeException("Could not initialize the Spdt class '$spdtCls'", error)
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

    @JvmStatic
    fun currentFlavor(): SpdtFlavor = of(SpdtFlavor::class.java)

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
}