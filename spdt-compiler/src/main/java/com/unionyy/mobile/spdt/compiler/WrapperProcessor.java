package com.unionyy.mobile.spdt.compiler;

import com.google.auto.service.AutoService;
import com.unionyy.mobile.spdt.compiler.expect.ExpectProcessor;
import com.unionyy.mobile.spdt.compiler.flavor.FlavorProcessor;
import com.unionyy.mobile.spdt.compiler.inject.InjectProcessor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "com.unionyy.mobile.spdt.annotation.SpdtExpect",
        "com.unionyy.mobile.spdt.annotation.SpdtActual",
        "com.unionyy.mobile.spdt.annotation.SpdtInject"
})
public class WrapperProcessor extends AbstractProcessor {

    private List<SpdtProcessor> list = Arrays.<SpdtProcessor>asList(
            new ExpectProcessor(),
            new FlavorProcessor(),
            new InjectProcessor()
    );

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Env env = new Env();
        env.filer = processingEnv.getFiler();
        env.elements = processingEnv.getElementUtils();
        env.logger = new Logger(processingEnv.getMessager());
        env.types = processingEnv.getTypeUtils();
        env.options = processingEnv.getOptions();

        env.packageName = "com.unionyy.mobile.spdt";

        try {
            for (SpdtProcessor p : list) {
                p.processingEnv = processingEnv;
                p.process(env, set, roundEnvironment);
            }
        } catch (Exception e) {
            env.logger.error(e.toString());
        }
        return false;
    }
}
