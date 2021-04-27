package com.unionyy.mobile.spdt.library

import com.unionyy.mobile.spdt.annotation.ProfessionalEdition
import com.unionyy.mobile.spdt.annotation.SpdtActual

/**
 * @author YvesCheung
 * 4/27/21
 */
@SpdtActual(ProfessionalEdition::class)
class ProFunction : SomeFunction {

    override fun call(): String = "Professional edition"
}