package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.SpdtFlavor

object Spdt {

    @JvmStatic
    fun inject(obj: Any) {
        val injectClsPackage = "com.unionyy.mobile.spdt.${obj.javaClass.simpleName}\$\$SpdtInjector"
        try {
            Class.forName(injectClsPackage)
                    .getMethod("inject", obj.javaClass)
                    .invoke(null, obj)
        } catch (e: ClassNotFoundException) {
            // 未使用注解，调用此方法会导致ClassNotFound异常
            e.printStackTrace()
        }

    }

    @JvmStatic
    inline fun <reified Spdt : Any> inflate(): Spdt = inflateOrNull()
            ?: throw RuntimeException("Could not initialize the Spdt class '${Spdt::class}'")

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    inline fun <reified Spdt : Any> inflateOrNull(): Spdt? {
        val spdtCls = Spdt::class.java
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
    fun currentFlavor(): SpdtFlavor = inflate()
}