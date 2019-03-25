package com.unionyy.mobile.spdt.skin.factory

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.unionyy.mobile.spdt.skin.factory.SpdtSkinFactory

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal class SpdtFactoryDelegate(private val factoryList: List<SpdtSkinFactory>) : LayoutInflater.Factory2 {

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
        for (factory in factoryList) {
            val view = factory.onCreateView(parent, name, context, attrs)
            if (view != null) {
                return view
            }
        }
        return null
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? =
        onCreateView(null, name, context, attrs)

}