package com.unionyy.mobile.plugin

import com.unionyy.mobile.spdt.data.SpdtFlavorData
import org.gradle.api.Namer
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.internal.reflect.Instantiator

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class DefaultSpdtConfigContainer extends AbstractNamedDomainObjectContainer<SpdtFlavorData>
        implements SpdtConfigContainer {

    DefaultSpdtConfigContainer(Instantiator instantiator, Namer<SpdtFlavorData> namer, CollectionCallbackActionDecorator callback) {
        super(SpdtFlavorData.class, instantiator, namer, callback)
    }

    @Override
    protected SpdtFlavorData doCreate(String s) {
        new SpdtFlavorData(flavorName: s)
    }

    //Override
    SpdtFlavorData current
}
