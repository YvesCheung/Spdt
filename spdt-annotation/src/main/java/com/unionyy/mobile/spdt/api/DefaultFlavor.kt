package com.unionyy.mobile.spdt.api

import com.unionyy.mobile.spdt.annotation.SpdtFlavor

class DefaultFlavor private constructor() : SpdtFlavor {

    override val appid: String = "default"

    override val resourceSuffix: String = ""
}