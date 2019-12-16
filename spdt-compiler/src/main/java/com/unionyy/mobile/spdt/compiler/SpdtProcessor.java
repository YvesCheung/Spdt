package com.unionyy.mobile.spdt.compiler;

import com.google.auto.service.AutoService;
import com.unionyy.mobile.spdt.compiler.expect.ActualFactoryProcessor;
import com.unionyy.mobile.spdt.compiler.expect.ExpectProcessor;
import com.unionyy.mobile.spdt.compiler.inject.InjectProcessor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
@AutoService(Processor.class)//自动生成 javax.annotation.processing.IProcessor 文件
@SupportedSourceVersion(SourceVersion.RELEASE_7)//java版本支持
public class SpdtProcessor extends AbstractProcessor {

    private List<IProcessor> processors = Arrays.<IProcessor>asList(
            //new FlavorProcessor(), //Use SpdtPlugin to generate the flavor class instead.
            new ExpectProcessor(),
            new InjectProcessor(),
            new ActualFactoryProcessor()
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
            for (IProcessor p : processors) {
                p.process(env, set, roundEnvironment);
            }
        } catch (Exception e) {
            env.logger.error(e.toString());
        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        for (IProcessor p : processors) {
            set.addAll(p.getSupportAnnotations());
        }
        return set;
    }
}
