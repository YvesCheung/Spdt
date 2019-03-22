package com.unionyy.mobile.spdt

import android.app.Application

/**
 * Created by 张宇 on 2019/3/7.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Spdt.applySkin(this)
    }
}