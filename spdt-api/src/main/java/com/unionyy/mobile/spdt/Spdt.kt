package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.SpdtFlavor
import java.lang.Exception
import java.lang.RuntimeException

object Spdt {

    @JvmStatic
    fun inject(obj: Any) {


    }

    @JvmStatic
    inline fun <reified Spdt : Any> inflate(): Spdt =
        inflateOrNull() ?: throw RuntimeException("Could not initialize the Spdt class '${Spdt::class}'")

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