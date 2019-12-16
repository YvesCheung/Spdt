package com.unionyy.mobile.spdt.annotation

/**
 * @author YvesCheung
 * 2019-12-16
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class SpdtIndex(val clsName: String)