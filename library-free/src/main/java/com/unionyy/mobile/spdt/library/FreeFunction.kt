package com.unionyy.mobile.spdt.library

import com.unionyy.mobile.spdt.annotation.FreeEdition
import com.unionyy.mobile.spdt.annotation.SpdtActual

/**
 * @author YvesCheung
 * 4/27/21
 */
@SpdtActual(FreeEdition::class)
class FreeFunction : SomeFunction {

    override fun call(): String = "Free edition"
}