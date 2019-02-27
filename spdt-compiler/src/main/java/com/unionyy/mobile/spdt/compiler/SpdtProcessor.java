package com.unionyy.mobile.spdt.compiler;

import com.google.auto.service.AutoService;
import com.unionyy.mobile.spdt.compiler.expect.ExpectProcessor;
import com.unionyy.mobile.spdt.compiler.flavor.FlavorProcessor;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 * <p>
 * https://youtrack.jetbrains.com/issue/KT-17883
 * <p>
 * https://github.com/square/kotlinpoet/issues/105
 * kotlinpoet now is not support write the **.kt to filer
 * <p>
 * apt 最好还是不要生成.kt文件阿
 */
public abstract class SpdtProcessor extends AbstractProcessor {

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
            process(env, set, roundEnvironment);
        } catch (Exception e) {
            env.logger.error(e.toString());
        }
        return false;
    }

    protected abstract void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception;
}
