package com.unionyy.mobile.plugin

import com.google.gson.annotations.SerializedName

class SpdtConfigData {

    @SerializedName("flavors")
    List<SpdtFlavor> flavors;

    @SerializedName("current")
    String current;
}
