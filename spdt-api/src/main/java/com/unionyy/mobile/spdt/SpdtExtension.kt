package com.unionyy.mobile.spdt

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : Any> spdtInject(): ReadOnlyProperty<Any, T> = SpdtProxy(T::class.java)

class SpdtProxy<SpdtObj : Any>(private val cls: Class<SpdtObj>) : ReadOnlyProperty<Any, SpdtObj> {

    private var _value: SpdtObj? = null

    override fun getValue(thisRef: Any, property: KProperty<*>): SpdtObj =
        _value ?: Spdt.of(cls).also { _value = it }
}