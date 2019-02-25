package com.unionyy.mobile.plugin

import com.unionyy.mobile.spdt.data.SpdtFlavorData
import org.gradle.api.NamedDomainObjectContainer

interface SpdtConfigContainer extends NamedDomainObjectContainer<SpdtFlavorData> {

    void setCurrent(SpdtFlavorData currentFlavor)

    SpdtFlavorData getCurrent()
}
