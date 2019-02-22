package com.unionyy.mobile.spdt.compiler;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

public interface IProcessor {

    void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception;

    List<String> getSupportedAnnotationTypes();
}
