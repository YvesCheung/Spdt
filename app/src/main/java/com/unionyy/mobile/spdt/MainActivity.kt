package com.unionyy.mobile.spdt

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import com.unionyy.mobile.spdt.annotation.SpdtInject
import com.unionyy.mobile.spdt.diffpackage.B
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

//    private val getter = Spdt.currentFlavor()

    @SpdtInject
    lateinit var getter: AppidGetter

    @SpdtInject
    lateinit var `我是中文`: AppidGetter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Spdt.inject(this)

        textView.text = Test().test.appid + " " + 我是中文.appid

        com.unionyy.mobile.spdt.diffpackage.MainActivity().test(this)
    }

    class Test {

        val test: AppidGetter by spdtInject()
    }
}
