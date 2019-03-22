package com.unionyy.mobile.spdt.skin

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtSkinManager(spdtCtx: SpdtSkinContext) {

    init {
        SkinLifecycle.install(spdtCtx)
    }
}