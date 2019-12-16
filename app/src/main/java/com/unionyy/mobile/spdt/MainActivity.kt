package com.unionyy.mobile.spdt

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

//    private val getter = Spdt.currentFlavor()

    val getter: AppidGetter by spdtInject()

    val `我是中文`: AppidGetter by spdtInject()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.text = Test().test.appid + " " + 我是中文.appid

        com.unionyy.mobile.spdt.diffpackage.MainActivity().test(this)
    }

    class Test {

        val test: AppidGetter by spdtInject()
    }
}
