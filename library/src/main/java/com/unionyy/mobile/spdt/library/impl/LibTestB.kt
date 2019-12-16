package com.unionyy.mobile.spdt.library.impl

import com.unionyy.mobile.spdt.annotation.LaotieFlavor
import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.library.LibTest

/**
 * @author YvesCheung
 * 2019-12-16
 */
@SpdtActual(LaotieFlavor::class)
class LibTestB : LibTest {

    override fun a(): String = "LibTestB"
}