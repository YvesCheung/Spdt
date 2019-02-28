package com.unionyy.mobile.spdt.diffpackage

import com.unionyy.mobile.spdt.annotation.LaotieFlavor
import com.unionyy.mobile.spdt.annotation.SpdtActual

interface B {

    fun print(): String = "B"
}

@SpdtActual(LaotieFlavor::class)
class SpecialB : B {

    override fun print(): String = "SpecialB"
}