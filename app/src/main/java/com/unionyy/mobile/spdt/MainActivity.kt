package com.unionyy.mobile.spdt

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.unionyy.mobile.spdt.annotation.SpdtInject
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    private val getter = Spdt.currentFlavor()

    @SpdtInject
    lateinit var getter: AppidGetter

    @SpdtInject
    lateinit var `我是中文`: AppidGetter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Spdt.inject(this)
        textView.text = getter.appid
    }
}
