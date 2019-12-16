package com.unionyy.mobile.spdt.library.impl

import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.annotation.XiaoMiFlavor
import com.unionyy.mobile.spdt.library.LibTest

/**
 * @author YvesCheung
 * 2019-12-16
 */
@SpdtActual(XiaoMiFlavor::class)
class LibTestA : LibTest {

    override fun a(): String {
        return "LibTestA"
    }
}