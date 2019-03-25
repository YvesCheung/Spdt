package com.unionyy.mobile.spdt.skin.core

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.view.LayoutInflater
import com.unionyy.mobile.spdt.annotation.SpdtSkin
import com.unionyy.mobile.spdt.skin.factory.SpdtFactoryDelegate

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal object SkinLifecycle {

    fun install(spdtContext: SpdtSkinContext) {
        val factoryDelegate = SpdtFactoryDelegate(spdtContext.factoryList)
        val app = spdtContext.app

        installLayoutFactory(app, factoryDelegate)
        app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                if (isContextSkinEnable(activity, spdtContext)) {
                    installLayoutFactory(activity, factoryDelegate)
                }
            }
        })
    }

    private fun isContextSkinEnable(context: Context, spdtContext: SpdtSkinContext): Boolean {
        return spdtContext.allActivityEnable
            || context.javaClass.getAnnotation(SpdtSkin::class.java) != null
    }

    private fun installLayoutFactory(context: Context, delegate: SpdtFactoryDelegate) {
        val inflater = LayoutInflater.from(context)
        LayoutInflaterCompat.setFactory2(inflater, delegate)
    }
}