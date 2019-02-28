package com.unionyy.mobile.spdt.compiler.inject;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Symbol;
import com.unionyy.mobile.spdt.annotation.SpdtInject;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;
import com.unionyy.mobile.spdt.compiler.Logger;

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

        Map<Element, Set<VariableElement>> classifiedElements = classifySymbols(injectedSymbols);

        generateInjectorClass(classifiedElements, env.filer);
    }

    private Map<Element, Set<VariableElement>> classifySymbols(Set<VariableElement> injectedSymbols) {
        Map<Element, Set<VariableElement>> result = new LinkedHashMap<>();
        for (VariableElement element : injectedSymbols) {
            Element classElement = element.getEnclosingElement();
            Set<VariableElement> variableElements = result.get(classElement);
            if (variableElements == null) {
                variableElements = new LinkedHashSet<>();
                result.put(classElement, variableElements);
            }
            variableElements.add(element);
        }
        return result;
    }

    private void generateInjectorClass(Map<Element, Set<VariableElement>> classifiedElements, Filer filer) {
        for (Element classElement : classifiedElements.keySet()) {
            MethodSpec.Builder methodBuild = MethodSpec
                    .methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassName.get(classElement.asType()), "target")
                    .returns(TypeName.VOID);

            for (VariableElement element : classifiedElements.get(classElement)) {
                String elementName = element.getSimpleName().toString();
                TypeName factoryCls = ClassName.get(((Symbol.VarSymbol) element).type);

                // 变量不能被修饰为private
                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    logger.error(String.format("The property '%s' of '%s' should not be modified" +
                            " with private.", elementName, classElement.getSimpleName()));
                }

                methodBuild.addStatement("target.$N = new $N().create()",
                        elementName,
                        factoryCls + "$$SpdtFactory");
            }

            TypeSpec injectCls = TypeSpec
                    .classBuilder(classElement.getSimpleName() + "$$SpdtInjector")
                    .addModifiers(Modifier.FINAL)
                    .addMethod(methodBuild.build())
                    .build();

            try {
                JavaFile.builder(((Symbol.ClassSymbol) classElement).packge().toString(), injectCls).build().writeTo(filer);
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