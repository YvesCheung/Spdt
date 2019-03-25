package com.unionyy.mobile.spdt.skin.log

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
interface SpdtLog {

    fun info(tag: String, msg: Any?)

    fun error(tag: String, msg: Any?)

    fun debug(tag: String, msg: Any?)
}