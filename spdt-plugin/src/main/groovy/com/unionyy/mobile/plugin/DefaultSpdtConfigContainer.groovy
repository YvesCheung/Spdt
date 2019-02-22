package com.unionyy.mobile.plugin

import org.gradle.api.Namer
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class DefaultSpdtConfigContainer extends AbstractNamedDomainObjectContainer<SpdtFlavor>
        implements SpdtConfigContainer {

    DefaultSpdtConfigContainer(Instantiator instantiator, Namer<SpdtFlavor> namer) {
        super(SpdtFlavor, instantiator, namer)
    }

    @Override
    protected SpdtFlavor doCreate(String s) {
        new SpdtFlavor(flavorName: s)
    }

    SpdtFlavor current

    @Override
    void current(SpdtFlavor currentFlavor) {
        current = currentFlavor
    }

    @Override
    SpdtFlavor getCurrent() {
        return current
    }
}
