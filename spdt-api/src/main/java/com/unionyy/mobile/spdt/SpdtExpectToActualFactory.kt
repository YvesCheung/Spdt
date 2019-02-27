package com.unionyy.mobile.spdt

interface SpdtExpectToActualFactory<API : Any> {

    fun create(): API?
}