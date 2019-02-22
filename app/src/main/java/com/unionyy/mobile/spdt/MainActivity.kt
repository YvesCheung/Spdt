package com.unionyy.mobile.spdt

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.unionyy.mobile.spdt.annotation.SpdtExpect
import com.unionyy.mobile.spdt.annotation.SpdtInject

class MainActivity : AppCompatActivity() {

    @SpdtExpect
    interface A

    class B : A

    @SpdtInject
    private lateinit var a: A

    @SpdtInject
    var c: A? = null

    @SpdtInject
    private val b: A? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        @SpdtInject
        var c: A? = null

        setContentView(R.layout.activity_main)
    }
}
