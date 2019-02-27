package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.LaotieFlavor
import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.annotation.XiaoMiFlavor

interface AppidGetter {

    val appid: String
}

@SpdtActual(LaotieFlavor::class)
class LaotieGetter : AppidGetter {

    override val appid: String
        get() = "yym141and"
}

@SpdtActual(XiaoMiFlavor::class)
class XiaomiGetter : AppidGetter {

    override val appid: String
        get() = "yym139and"
}

