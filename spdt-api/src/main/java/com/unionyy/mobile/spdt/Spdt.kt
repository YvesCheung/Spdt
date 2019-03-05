package com.unionyy.mobile.spdt

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
        val clsName = "${spdtCls.canonicalName}\$\$SpdtFactory"
        return try {
            val factory: SpdtExpectToActualFactory<Spdt> =
                Class.forName(clsName).newInstance()
                    as SpdtExpectToActualFactory<Spdt>
            factory.create()
                ?: throw RuntimeException("The create() method in '$clsName' return null.")
        } catch (ignore: Exception) {
            throw RuntimeException("Could not initialize the Spdt class '$spdtCls'")
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <Spdt : Any> ofOrNull(spdtCls: Class<Spdt>): Spdt? {
        val clsName = "${spdtCls.canonicalName}\$\$SpdtFactory"
        return try {
            val factory: SpdtExpectToActualFactory<Spdt>? =
                Class.forName(clsName).newInstance()
                    as? SpdtExpectToActualFactory<Spdt>
            factory?.create()
        } catch (ignore: Exception) {
            null
        }
    }

    @JvmStatic
    fun currentFlavor(): SpdtFlavor = of(SpdtFlavor::class.java)
}