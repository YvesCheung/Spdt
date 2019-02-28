package com.unionyy.mobile.spdt.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class SpdtActual(
    val value: KClass<out SpdtFlavor>
)