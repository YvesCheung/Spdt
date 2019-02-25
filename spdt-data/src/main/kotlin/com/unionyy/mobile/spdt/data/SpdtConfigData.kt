package com.unionyy.mobile.spdt.data

import com.google.gson.annotations.SerializedName

data class SpdtConfigData(

    @SerializedName("flavors")
    val flavors: List<SpdtFlavorData> = listOf(),

    @SerializedName("current")
    var currentFlavor: String
)