package com.unionyy.mobile.spdt.skin

import android.app.Application
import com.unionyy.mobile.spdt.skin.factory.SpdtSkinFactory

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface SpdtSkinOption {

    val app: Application

    fun factorys(option: (SpdtSkinFactoryOption) -> Unit)

    fun factorys(vararg factory: SpdtSkinFactory)

    var allActivityEnable: Boolean
}

interface SpdtSkinFactoryOption {

    fun addFirst(factory: SpdtSkinFactory)

    fun addLast(factory: SpdtSkinFactory)
}