package com.unionyy.mobile.spdt

import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.annotation.SpdtExpect
import com.unionyy.mobile.spdt.annotation.XiaoMiFlavor
import java.io.Serializable

class OuterClass {

    @SpdtExpect
    interface InnerClass {

        fun haha(): String
    }

    @SpdtActual(XiaoMiFlavor::class)
    class InnerB : InnerClass, Cloneable, Serializable {

        override fun haha(): String {
            return "B"
        }
    }
}