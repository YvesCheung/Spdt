package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.annotation.SpdtExpect
import com.unionyy.mobile.spdt.annotation.XiaoMiFlavor
import com.unionyy.mobile.spdt.api.DefaultFlavor
import java.io.Serializable

class OuterClass {

    @SpdtExpect
    interface InnerClass {

        fun haha(): String
    }

    @SpdtActual(values = [XiaoMiFlavor::class, DefaultFlavor::class])
    class InnerB : InnerClass, Cloneable, Serializable {

        override fun haha(): String {
            return "B"
        }
    }
}