package com.yy.mobile.uniondif

import com.unionyy.mobile.spdt.annotation.SpdtExpect
import com.unionyy.mobile.spdt.annotation.SpdtInject

/**
 * Created by wangfeihang on 2019/1/23.
 */
class OuterClass {
    object AppIdConfig {

        @SpdtExpect
        interface AppIds {
            val hiidoAppKey: String
            val udbAppId: String
            val crashSdkAppId: String
            val sceneId: Int
            val serviceProtocolAppId: String
        }

        @JvmStatic
        @SpdtInject
        lateinit var appIds: AppIds
    }
}