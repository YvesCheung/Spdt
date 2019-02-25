package com.unionyy.mobile.spdt.data

import com.google.gson.annotations.SerializedName

data class SpdtFlavorData(
    @SerializedName("name")
    var flavorName: String = "YYFlavor",
    @SerializedName("appid")
    var appid: String = "yymand"
)