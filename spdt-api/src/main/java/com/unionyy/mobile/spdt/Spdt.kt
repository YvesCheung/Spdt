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
        } catch (e: ClassNotFoundException) {
            // 未使用注解，调用此方法会导致ClassNotFound异常
            e.printStackTrace()
        }

    }

    @JvmStatic
    fun <Spdt : Any> of(spdtCls: Class<Spdt>): Spdt = ofOrNull(spdtCls)
            ?: throw RuntimeException("Could not initialize the Spdt class '$spdtCls'")

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