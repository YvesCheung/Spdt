package com.unionyy.mobile.spdt

import android.app.Application
import android.os.Looper
import android.util.Log
import android.util.LruCache
import com.unionyy.mobile.spdt.annotation.SpdtFlavor
import com.unionyy.mobile.spdt.skin.SpdtSkinContext
import com.unionyy.mobile.spdt.skin.SpdtSkinFactoryOption
import com.unionyy.mobile.spdt.skin.SpdtSkinManager
import com.unionyy.mobile.spdt.skin.SpdtSkinOption
import com.unionyy.mobile.spdt.skin.factory.DefaultSkinFactory
import com.unionyy.mobile.spdt.skin.factory.SpdtSkinFactory
import com.unionyy.mobile.spdt.skin.log.SpdtLog
import java.util.*

object Spdt {

    @JvmStatic
    fun inject(obj: Any) {
        val injectCls = "${obj.javaClass.name}\$\$SpdtInjector"
        try {
            Class.forName(injectCls)
                .getMethod("inject", obj.javaClass)
                .invoke(null, obj)
        } catch (e: Exception) {
            throw SpdtException("Could not Spdt#inject the object '$obj' " +
                "because '$injectCls' is invalid.", e)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <Spdt : Any> of(spdtCls: Class<Spdt>): Spdt {
        val clsName = "${spdtCls.name}\$\$SpdtFactory"
        return try {
            val factory: SpdtExpectToActualFactory<Spdt> = getOrNew(clsName) {
                Class.forName(clsName).newInstance() as SpdtExpectToActualFactory<Spdt>
            } ?: throw SpdtException("The method getOrNew('$clsName') return null.")

            factory.create()
                ?: throw SpdtException("The create() method in '$clsName' return null.")
        } catch (error: Exception) {
            throw SpdtException("Could not initialize the Spdt class '$spdtCls'", error)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    fun <Spdt : Any> ofOrNull(spdtCls: Class<Spdt>): Spdt? {
        val clsName = "${spdtCls.name}\$\$SpdtFactory"
        return try {
            val factory: SpdtExpectToActualFactory<Spdt>? = getOrNew(clsName) {
                Class.forName(clsName).newInstance() as SpdtExpectToActualFactory<Spdt>
            }

            factory?.create()
        } catch (ignore: Exception) {
            null
        }
    }

    @JvmStatic
    fun currentFlavor(): SpdtFlavor = of(SpdtFlavor::class.java)

    private val cache = LruCache<String, SpdtExpectToActualFactory<*>>(100)

    @Suppress("UNCHECKED_CAST")
    private inline fun <Spdt : Any> getOrNew(
        clsName: String,
        newInstance: () -> SpdtExpectToActualFactory<Spdt>?
    ): SpdtExpectToActualFactory<Spdt>? {
        val factory: SpdtExpectToActualFactory<*>? = cache[clsName]
        return if (factory == null) {
            newInstance().also {
                cache.put(clsName, it)
            }
        } else {
            factory as SpdtExpectToActualFactory<Spdt>
        }
    }

    private var skinManager: SpdtSkinManager? = null

    @JvmStatic
    @JvmOverloads
    fun applySkin(app: Application, option: ((SpdtSkinOption) -> Unit)? = null): SpdtSkinManager {
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
                Log.i(tag, msg?.toString())
            }

            override fun error(tag: String, msg: Any?) {
                Log.e(tag, msg?.toString())
            }

            override fun debug(tag: String, msg: Any?) {
                Log.d(tag, msg?.toString())
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
}