package com.unionyy.mobile.spdt

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> spdtInject(): ReadOnlyProperty<Any, T> = SpdtProxy(T::class.java)

inline fun <reified T : Any> spdtInjectNullable(): ReadOnlyProperty<Any, T?> = SpdtProxyNullable(T::class.java)

class SpdtProxy<SpdtObj : Any>(private val cls: Class<SpdtObj>) : ReadOnlyProperty<Any, SpdtObj> {

    private var _value: SpdtObj? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): SpdtObj =
        _value ?: Spdt.of(cls).also { _value = it }
}

class SpdtProxyNullable<SpdtObj : Any>(private val cls: Class<SpdtObj>) : ReadOnlyProperty<Any, SpdtObj?> {

    private var isInit = false
    private var _value: SpdtObj? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): SpdtObj? {
        if (isInit) {
            return _value
        } else {
            _value = Spdt.ofOrNull(cls)
            isInit = true
            return _value
        }
    }
}