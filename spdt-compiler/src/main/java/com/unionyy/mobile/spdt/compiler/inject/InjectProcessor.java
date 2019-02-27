package com.unionyy.mobile.spdt.compiler.inject;

import com.google.auto.service.AutoService;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.SpdtProcessor;

import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes(
        "com.unionyy.mobile.spdt.annotation.SpdtInject"
)
public class InjectProcessor extends SpdtProcessor {

    @Override
    protected void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {

    }
}
