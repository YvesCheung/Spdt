package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.SpdtFlavor

object Spdt {

    @JvmStatic
    fun inject(obj: Any) {


    }

    @JvmStatic
    fun <Spdt : Any> of(spdtCls: Class<Spdt>): Spdt {
        TODO()
    }

    @JvmStatic
    fun <Spdt : Any> ofOrNull(spdtCls: Class<Spdt>): Spdt? {
        TODO()
    }

    @JvmStatic
    fun currentFlavor(): SpdtFlavor = of(SpdtFlavor::class.java)
}