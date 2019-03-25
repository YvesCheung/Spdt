package com.unionyy.mobile.spdt.skin.core

import android.app.Application
import com.unionyy.mobile.spdt.skin.factory.SpdtSkinFactory
import com.unionyy.mobile.spdt.skin.log.SpdtLog

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
data class SpdtSkinContext(
    val app: Application,
    val factoryList: List<SpdtSkinFactory>,
    val allActivityEnable: Boolean,
    val logger: SpdtLog
)