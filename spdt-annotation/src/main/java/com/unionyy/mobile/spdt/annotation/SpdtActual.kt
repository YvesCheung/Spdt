package com.unionyy.mobile.spdt.annotation

import com.unionyy.mobile.spdt.api.DefaultFlavor
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
annotation class SpdtActual(
    val value: KClass<out SpdtFlavor> = DefaultFlavor::class,
    val values: Array<KClass<out SpdtFlavor>> = []
)