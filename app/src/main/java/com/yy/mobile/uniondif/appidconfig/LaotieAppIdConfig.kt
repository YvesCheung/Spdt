package com.yy.mobile.uniondif.appidconfig

import com.unionyy.mobile.spdt.annotation.LaotieFlavor
import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.yy.mobile.uniondif.OuterClass.AppIdConfig


/**
 * Created by wangfeihang on 2019/1/23.
 */
@SpdtActual(LaotieFlavor::class)
class LaotieAppIdConfig : AppIdConfig.AppIds {
    override val udbAppId: String = "yym141and"
    override val crashSdkAppId: String = "yym141and"
    override val sceneId: Int = 1022
    override val serviceProtocolAppId: String = "yym141and"
    override val hiidoAppKey: String = "613de4d483b0d95579d396a5c5736833"
}