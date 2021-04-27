package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.FreeEdition
import com.unionyy.mobile.spdt.annotation.ProfessionalEdition
import com.unionyy.mobile.spdt.annotation.SpdtActual

interface AppidGetter {

    val appid: String
}

@SpdtActual(ProfessionalEdition::class)
class ProfessionalAppidGetter : AppidGetter {

    override val appid: String
        get() = "1001"
}

@SpdtActual(FreeEdition::class)
class FreeAppidGetter : AppidGetter {

    override val appid: String
        get() = "1002"
}

