package com.unionyy.mobile.spdt.skin

import android.app.Application
import com.unionyy.mobile.spdt.skin.core.SpdtSkinManager

/**
 * Created by 张宇 on 2019/3/25.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface SkinClient : SkinResource {

    fun applySkin(app: Application, option: (SpdtSkinOption.() -> Unit)?): SpdtSkinManager

    fun applySkin(app: Application): SpdtSkinManager
}