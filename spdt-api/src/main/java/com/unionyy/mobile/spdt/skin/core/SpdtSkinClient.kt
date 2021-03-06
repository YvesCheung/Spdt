package com.unionyy.mobile.spdt.skin.core

import android.app.Application
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Looper
import android.util.Log
import com.unionyy.mobile.spdt.SpdtException
import com.unionyy.mobile.spdt.skin.SkinClient
import com.unionyy.mobile.spdt.skin.SpdtSkinFactoryOption
import com.unionyy.mobile.spdt.skin.SpdtSkinOption
import com.unionyy.mobile.spdt.skin.factory.DefaultSkinFactory
import com.unionyy.mobile.spdt.skin.factory.SpdtSkinFactory
import com.unionyy.mobile.spdt.skin.log.SpdtLog
import java.util.*

/**
 * Created by 张宇 on 2019/3/25.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal class SpdtSkinClient : SkinClient {

    //要加个条件，support模块引入时用support的skinFactory 和  support的skinManager
    private var skinManager: SpdtSkinManager? = null

    override fun applySkin(app: Application): SpdtSkinManager =
        applySkin(app, null)

    override fun applySkin(app: Application, option: ((SpdtSkinOption) -> Unit)?): SpdtSkinManager {
        checkMainThread("applySkin")

        val instance = skinManager
        return if (instance == null) {
            val ctxProducer = SpdtSkinOptionImpl(app)
            option?.invoke(ctxProducer)
            SpdtSkinManager(ctxProducer.produceContext()).also { skinManager = it }
        } else {
            instance
        }
    }

    private fun checkMainThread(methodName: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw SpdtException("This method '$methodName' can only run in the UI thread.")
        }
    }

    private class SpdtSkinOptionImpl(override val app: Application) : SpdtSkinOption {

        private val factoryList = LinkedList<SpdtSkinFactory>().apply {
            add(DefaultSkinFactory())
        }

        private val factoryManager = FactoryManager()

        override fun factorys(option: (SpdtSkinFactoryOption) -> Unit) {
            option(factoryManager)
        }

        override fun factorys(vararg factory: SpdtSkinFactory) {
            factory.forEach(factoryManager::addLast)
        }

        override var allActivityEnable: Boolean = true

        override var logger: SpdtLog = object : SpdtLog {
            override fun info(tag: String, msg: Any?) {
                Log.i(tag, msg.toString())
            }

            override fun error(tag: String, msg: Any?) {
                Log.e(tag, msg.toString())
            }

            override fun debug(tag: String, msg: Any?) {
                Log.d(tag, msg.toString())
            }
        }

        fun produceContext(): SpdtSkinContext =
            SpdtSkinContext(app, factoryList.toList(), allActivityEnable, logger)

        private inner class FactoryManager : SpdtSkinFactoryOption {
            private val checkDuplicate = mutableSetOf<SpdtSkinFactory>()

            override fun addFirst(factory: SpdtSkinFactory) = checkDuplicate(factory) {
                factoryList.addFirst(factory)
            }

            override fun addLast(factory: SpdtSkinFactory) = checkDuplicate(factory) {
                factoryList.addLast(factory)
            }

            private inline fun checkDuplicate(factory: SpdtSkinFactory, isOk: () -> Unit) {
                if (checkDuplicate.contains(factory)) {
                    throw SpdtException("SpdtSkinFactory '$factory' can't be register twice!")
                }
                isOk()
                checkDuplicate.add(factory)
            }
        }
    }

    override fun getDrawable(context: Context, resId: Int): Drawable =
        delegate("getDrawable") { getDrawable(context, resId) }

    override fun getDrawable(resId: Int): Drawable =
        delegate("getDrawable") { getDrawable(resId) }

    override fun getColorStateList(context: Context, resId: Int): ColorStateList =
        delegate("getColorStateList") { getColorStateList(context, resId) }

    override fun getColorStateList(resId: Int): ColorStateList =
        delegate("getColorStateList") { getColorStateList(resId) }

    override fun getColor(context: Context, resId: Int): Int =
        delegate("getColor") { getColor(context, resId) }

    override fun getColor(resId: Int): Int =
        delegate("getColor") { getColor(resId) }

    override fun getString(context: Context, resId: Int): String =
        delegate("getString") { getString(context, resId) }

    override fun getString(resId: Int): String =
        delegate("getString") { getString(resId) }

    private inline fun <T> delegate(methodName: String, doActual: SpdtSkinManager.() -> T): T {
        val actual = skinManager
            ?: throw SpdtException("Can't use 'Spdt#$methodName' without 'Spdt#applySkin'.")
        return doActual(actual)
    }
}