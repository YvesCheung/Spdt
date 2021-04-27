package com.unionyy.mobile.spdt

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import com.unionyy.mobile.spdt.library.SomeFunction
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    val function: SomeFunction by spdtInject()

    val `我是中文`: AppidGetter by spdtInject()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView.text = function.call() + 我是中文.appid
    }
}
