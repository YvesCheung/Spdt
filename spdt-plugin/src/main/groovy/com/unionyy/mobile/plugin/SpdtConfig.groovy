package com.unionyy.mobile.plugin

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtConfig {

    List<SpdtFlavorContainer> flavors = []

    void create(String flavorName,
                @DelegatesTo(
                        value = SpdtFlavorContainer,
                        strategy = Closure.DELEGATE_FIRST
                ) Closure closure) {

        def container = new SpdtFlavorContainer(flavorName: flavorName)
        closure.delegate = container
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.call()
        flavors += container
    }
}
