package com.unionyy.mobile.spdt.diffpackage

import com.unionyy.mobile.spdt.annotation.LaotieFlavor
import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.annotation.XiaoMiFlavor

interface AppidGetter {

    val appid: String
}

@SpdtActual(values = [LaotieFlavor::class, XiaoMiFlavor::class])
class LaotieGetter : AppidGetter {

    override val appid: String
        get() = "yym141and"
}

@SpdtActual
class DefaultGetter : AppidGetter {

    override val appid: String = "yym"
}