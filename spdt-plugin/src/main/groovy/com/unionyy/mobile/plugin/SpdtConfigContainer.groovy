package com.unionyy.mobile.plugin

import org.gradle.api.NamedDomainObjectContainer

interface SpdtConfigContainer extends NamedDomainObjectContainer<SpdtFlavor> {

    void current(SpdtFlavor currentFlavor)

    SpdtFlavor getCurrent()
}
