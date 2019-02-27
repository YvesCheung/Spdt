package com.unionyy.mobile.spdt.compiler.inject;

import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public class InjectProcessor implements IProcessor {

    @Override
    public void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {

    }

    @Override
    public Collection<String> getSupportAnnotations() {
        return Collections.singletonList(
                "com.unionyy.mobile.spdt.annotation.SpdtInject");
    }
}
