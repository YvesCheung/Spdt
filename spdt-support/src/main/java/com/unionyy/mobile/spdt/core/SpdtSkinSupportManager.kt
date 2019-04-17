package com.unionyy.mobile.spdt.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.content.res.AppCompatResources
import com.unionyy.mobile.spdt.skin.core.SpdtSkinContext
import com.unionyy.mobile.spdt.skin.core.SpdtSkinManager

/**
 * Created BY PYF 2019/4/16
 * email: pengyangfan@yy.com
 */
class SpdtSkinSupportManager(private val spdtCtx: SpdtSkinContext) : SpdtSkinManager(spdtCtx) {

    private val invalidID = 0

    override fun getDrawable(context: Context, resId: Int): Drawable {
        //正常下只处理换肤情况，抄袭的哪个操作好多
        val realResId = getTargetResId(context, resId)
        if (realResId.resId != invalidID) {
            //看源码 应该是不会空的
            return AppCompatResources.getDrawable(context, resId)!!
        }
        return spdtCtx.app.resources.getDrawable(resId)
    }
}