package com.yy.mobile.uniondif.appidconfig

import com.unionyy.mobile.spdt.annotation.LaotieFlavor
import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.yy.mobile.uniondif.OuterClass.AppIdConfig


/**
 * Created by wangfeihang on 2019/1/23.
 */
@SpdtActual(LaotieFlavor::class)
class LaotieAppIdConfig : AppIdConfig.AppIds {
    override val udbAppId: String = "aaa"
    override val crashSdkAppId: String = "aaa"
    override val sceneId: Int = 1111
    override val serviceProtocolAppId: String = "aaa"
    override val hiidoAppKey: String = "aaa"
}