package com.unionyy.mobile.spdt

/**
 * Created by 张宇 on 2019/3/22.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class SpdtException : RuntimeException {

    constructor(msg: String) : super(msg)

    constructor(throwable: Throwable) : super(throwable)

    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}