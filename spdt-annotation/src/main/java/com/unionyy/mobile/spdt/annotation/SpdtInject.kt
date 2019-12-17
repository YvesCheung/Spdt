package com.unionyy.mobile.spdt.annotation

@Deprecated(message = "支持多Module后，不再支持inject注入实例")
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.BINARY)
annotation class SpdtInject