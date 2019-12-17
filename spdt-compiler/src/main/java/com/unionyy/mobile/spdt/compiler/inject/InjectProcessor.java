package com.unionyy.mobile.spdt.compiler.inject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.unionyy.mobile.spdt.annotation.SpdtInject;
import com.unionyy.mobile.spdt.annotation.SpdtKeep;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;
import com.unionyy.mobile.spdt.compiler.Logger;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

@Deprecated
public class InjectProcessor implements IProcessor {

    private Logger logger;

    @Override
    public void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {
        logger = env.logger;

        Set<VariableElement> injectedSymbols
                = ElementFilter.fieldsIn(roundEnvironment.getElementsAnnotatedWith(SpdtInject.class));

        Map<TypeElement, Set<VariableElement>> classifiedElements = classifySymbols(injectedSymbols);

        generateInjectorClass(classifiedElements, env.filer);
    }

    private Map<TypeElement, Set<VariableElement>> classifySymbols(@NotNull Set<VariableElement> injectedSymbols) {
        Map<TypeElement, Set<VariableElement>> result = new LinkedHashMap<>();
        for (VariableElement element : injectedSymbols) {
            Element classElement = element.getEnclosingElement();
            if (classElement instanceof TypeElement) {
                Set<VariableElement> variableElements = result.get(classElement);
                if (variableElements == null) {
                    variableElements = new LinkedHashSet<>();
                    result.put((TypeElement) classElement, variableElements);
                }
                variableElements.add(element);
            } else {
                logger.error("Why '" + classElement + "' is not a class?");
            }

        }
        return result;
    }

    private void generateInjectorClass(@NotNull Map<TypeElement, Set<VariableElement>> classifiedElements, Filer filer) {
        for (TypeElement classElement : classifiedElements.keySet()) {
            MethodSpec.Builder methodBuild = MethodSpec
                    .methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassName.get(classElement), "target")
                    .returns(TypeName.VOID);

            for (VariableElement element : classifiedElements.get(classElement)) {
                String elementName = element.getSimpleName().toString();

                ClassName variableType = (ClassName) ClassName.get(element.asType());
                ClassName factoryCls = ClassName.bestGuess(variableType.reflectionName() + "$$SpdtFactory");

                // 变量不能被修饰为private
                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    logger.error(String.format("The property '%s' of '%s' should not be modified" +
                            " with private.", elementName, classElement.getSimpleName()));
                }

                methodBuild.addStatement("target.$N = new $T().create()",
                        elementName, factoryCls);
            }
            String flatName = ClassName.get(classElement).reflectionName();
            TypeSpec injectCls = TypeSpec
                    .classBuilder(flatName.substring(flatName.lastIndexOf(".") + 1) + "$$SpdtInjector")
                    .addModifiers(Modifier.FINAL)
                    .addMethod(methodBuild.build())
                    .addAnnotation(SpdtKeep.class)
                    .build();

            try {
                JavaFile.builder(ClassName.get(classElement).packageName(), injectCls).build().writeTo(filer);
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
