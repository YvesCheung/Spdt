package com.unionyy.mobile.spdt.compiler.inject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.unionyy.mobile.spdt.annotation.SpdtInject;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;
import com.unionyy.mobile.spdt.compiler.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class InjectProcessor implements IProcessor {

    @Override
    public void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {
        Logger logger = env.logger;

        Set<? extends Element> variableElements
                = roundEnvironment.getElementsAnnotatedWith(SpdtInject.class);

        // classify
        Map<TypeElement, Set<Element>> classifiedElements = new HashMap<>();
        for (Element element : variableElements) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            Set<Element> elements = classifiedElements.get(typeElement);
            if (elements == null) {
                elements = new HashSet<>();
                classifiedElements.put(typeElement, elements);
            }
            elements.add(element);
        }

        // generate Java class
        TypeName factoryCls = ClassName.get(env.packageName, "AppidGetter$$SpdtFactory");
        for (TypeElement typeElement : classifiedElements.keySet()) {
            MethodSpec.Builder methodBuild = MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassName.get(typeElement.asType()), "activity")
                    .returns(TypeName.VOID);

            for (Element element : classifiedElements.get(typeElement)) {
                methodBuild.addStatement("activity.$N = new $T().create()",
                        element.getSimpleName().subSequence(0, element.getSimpleName().toString().indexOf("$")),
                        factoryCls);
            }

            TypeSpec injectCls = TypeSpec
                    .classBuilder(typeElement.getSimpleName() + "$$SpdtInjector")
                    .addModifiers(Modifier.FINAL)
                    .addMethod(methodBuild.build())
                    .build();

            try {
                JavaFile.builder(env.packageName, injectCls).build().writeTo(env.filer);
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }
    }

    @Override
    public Collection<String> getSupportAnnotations() {
        return Collections.singletonList(
                "com.unionyy.mobile.spdt.annotation.SpdtInject");
    }
}
