package com.unionyy.mobile.plugin

import com.google.gson.annotations.SerializedName

class SpdtFlavorContainer {

    @SerializedName("flavorName")
    String flavorName = "YYFlavor"

    @SerializedName("appid")
    String appid = "yymand"

    @Override
    String toString() {
        return "$flavorName(appid = $appid)"
    }
}
