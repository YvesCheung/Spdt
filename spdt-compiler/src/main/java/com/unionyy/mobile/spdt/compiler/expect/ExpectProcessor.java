package com.unionyy.mobile.spdt.compiler.expect;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.code.Attribute;
import com.sun.tools.javac.code.Attribute.UnresolvedClass;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.code.Type.ClassType;
import com.unionyy.mobile.spdt.annotation.SpdtActual;
import com.unionyy.mobile.spdt.annotation.SpdtExpect;
import com.unionyy.mobile.spdt.annotation.SpdtFlavor;
import com.unionyy.mobile.spdt.annotation.SpdtKeep;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;
import com.unionyy.mobile.spdt.compiler.Logger;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

public class ExpectProcessor implements IProcessor {

    private Logger log;

    @Override
    public void process(
            Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {
        log = env.logger;

        Set<TypeElement> actualClasses = ElementFilter.typesIn(
                roundEnvironment.getElementsAnnotatedWith(SpdtActual.class));

        Map<TypeMirror, Set<TypeElement>> mapExpectToActual = classifyActualClass(actualClasses);
        log.info(dump(mapExpectToActual), true);

        generateFactoryClass(env, mapExpectToActual);
    }

    private Map<TypeMirror, Set<TypeElement>> classifyActualClass(Set<TypeElement> actualClasses) {

        Map<TypeMirror, Set<TypeElement>> result = new LinkedHashMap<>();
        for (TypeElement actualCls : actualClasses) {
            List<? extends TypeMirror> interfaces = actualCls.getInterfaces();
            TypeMirror superCls = actualCls.getSuperclass();

            if (actualCls instanceof Type && ((Type) actualCls).isInterface()) {
                log.error("The type [" + actualCls + "] is annotated with @SpdtActual,\n" +
                        "so it can't be an interface.\n" +
                        "Use @SpdtActual class " + actualCls + " instead.");
            }
            if (actualCls.getModifiers().contains(Modifier.ABSTRACT)) {
                log.error("The type [" + actualCls + "] is annotated with @SpdtActual,\n" +
                        "so it can't be abstract.\n" +
                        "Use @SpdtActual class " + actualCls + " instead.");
            }
            if (interfaces.isEmpty()
                    && (superCls == null || isObject(superCls))) {
                log.error("The class [" + actualCls + "] is annotated with @SpdtActual,\n" +
                        "but it has no super class or interfaces.\n" +
                        "Which class or interfaces do you want 'Spdt' to bind?");
            }

            Set<TypeMirror> expectClasses = findExpectTarget(superCls, interfaces);
            if (expectClasses.isEmpty()) {

                Collection<? extends TypeMirror> superCandidate;
                if (isObject(superCls)) {
                    superCandidate = interfaces;
                } else {
                    Set<TypeMirror> tmp = new LinkedHashSet<>(interfaces);
                    tmp.add(superCls);
                    superCandidate = tmp;
                }

                if (superCandidate.isEmpty()) {
                    log.error("The class [" + actualCls + "] is annotated with @SpdtActual,\n" +
                            "but no @SpdtExpect target found.\n" +
                            "You should let it implement an interface.");
                } else {
                    log.error("The class [" + actualCls + "] is annotated with @SpdtActual,\n" +
                            "but no @SpdtExpect target found.\n" +
                            "You should add @SpdtExpect annotation to one of the following target:\n" +
                            "<" + superCandidate + ">.\n" +
                            "Or allow to implement only one interface without annotations.");
                }
            }

            for (TypeMirror expectCls : expectClasses) {
                Set<TypeElement> actualClsForThisExpect = result.get(expectCls);
                if (actualClsForThisExpect == null) {
                    actualClsForThisExpect = new LinkedHashSet<>();
                    result.put(expectCls, actualClsForThisExpect);
                }
                actualClsForThisExpect.add(actualCls);
            }
        }

        for (Entry<TypeMirror, Set<TypeElement>> entry : result.entrySet()) {
            TypeMirror expect = entry.getKey();
            Set<TypeElement> actuals = entry.getValue();

            Map<TypeMirrorWrapper, TypeElement> checkDuplicate = new LinkedHashMap<>(4);
            for (TypeElement actualCls : actuals) {
                TypeMirror flavor = getAnnotationParam(actualCls, SpdtActual.class, "value");
                if (flavor == null) { //error
                    return Collections.emptyMap();
                }
                TypeMirrorWrapper wrapper = TypeMirrorWrapper.of(flavor);
                TypeElement otherCls = checkDuplicate.get(wrapper);
                if (otherCls != null) {
                    log.error("The class [" + otherCls + "] and [" + actualCls + "] are \n" +
                            "annotated with the same annotation [@SpdtActual(" +
                            flavor + ")],\n" +
                            "and they implement the same interface or inherit the same base class " +
                            "@SpdtExpect [" + expect + "].\n" +
                            "Each flavor can have only one implementation class.");
                } else {
                    checkDuplicate.put(wrapper, actualCls);
                }
            }
        }
        return result;
    }

    private Set<TypeMirror> findExpectTarget(
            TypeMirror superClass,
            Collection<? extends TypeMirror> interfaces) {
        Set<TypeMirror> result = new LinkedHashSet<>();
        Set<TypeMirror> clsOrInterfaces = new LinkedHashSet<>();
        clsOrInterfaces.add(superClass);
        clsOrInterfaces.addAll(interfaces);

        for (TypeMirror cls : clsOrInterfaces) {
            if (cls instanceof ClassType) {
                SpdtExpect maybeNull = ((ClassType) cls).asElement()
                        .getAnnotation(SpdtExpect.class);
                if (maybeNull != null) {
                    result.add(cls);
                }
            }
        }

        //Trick: if no @SpdtExpect element，use the single interface.
        if (result.isEmpty()
                && isObject(superClass)
                && interfaces.size() == 1) {
            TypeMirror singleInterface = interfaces.iterator().next();
            result.add(singleInterface);
        }
        return result;
    }

    private void generateFactoryClass(Env env, Map<TypeMirror, Set<TypeElement>> mapExpectToActual) {
        for (Entry<TypeMirror, Set<TypeElement>> entry : mapExpectToActual.entrySet()) {
            TypeMirror expectCls = entry.getKey();
            TypeName expectClsName = TypeName.get(expectCls);
            TypeName spdtCls = ClassName.get(env.packageName, "Spdt");
            ClassName factoryName = ClassName.get(env.packageName, "SpdtExpectToActualFactory");

            MethodSpec.Builder createMethodBuilder =
                    MethodSpec.methodBuilder("create")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Nullable.class)
                            .addAnnotation(Override.class)
                            .addStatement("Class<? extends $T> flavorCls = " +
                                    "$T.currentFlavor().getClass()", SpdtFlavor.class, spdtCls)
                            .returns(expectClsName);

            for (TypeElement actualCls : entry.getValue()) {
                TypeMirror flavor = getAnnotationParam(actualCls, SpdtActual.class, "value");
                if (flavor == null) {
                    continue;
                }
                TypeName flavorName;
                if (flavor instanceof Type.ErrorType) {
                    String origin = flavor.toString();
                    ClassName guessCls = ClassName.bestGuess(origin.substring(origin.indexOf(".") + 1));
                    if (guessCls.packageName().isEmpty()) {
                        flavorName = ClassName.get(env.packageName + ".annotation", guessCls.toString());
                    } else {
                        flavorName = guessCls;
                    }
                } else {
                    flavorName = TypeName.get(flavor);
                }
                createMethodBuilder.addCode(
                        CodeBlock
                                .builder()
                                .beginControlFlow("if (flavorCls == $T.class)", flavorName)
                                .addStatement("return new $T()", actualCls)
                                .endControlFlow()
                                .build()
                );
            }

            MethodSpec createMethod = createMethodBuilder
                    .addStatement("return null")
                    .build();

            ParameterizedTypeName baseFactory = ParameterizedTypeName.get(factoryName, expectClsName);
            ClassName expectGuessName = ClassName.bestGuess(expectClsName.toString());
            String className = expectGuessName.reflectionName()
                    .replaceFirst(expectGuessName.packageName() + ".", "");
            TypeSpec factoryCls = TypeSpec
                    .classBuilder(className + "$$SpdtFactory")
                    .addSuperinterface(baseFactory)
                    .addModifiers(Modifier.FINAL)
                    .addMethod(createMethod)
                    .addAnnotation(SpdtKeep.class)
                    .build();

            try {
                JavaFile.builder(expectGuessName.packageName(), factoryCls).build().writeTo(env.filer);
            } catch (IOException e) {
                log.warn(e.getMessage());
            }
        }
    }

    private static boolean isObject(TypeMirror obj) {
        return obj instanceof ClassType
                && "java.lang.Object".equals(obj.toString());
    }

    private static AnnotationMirror getAnnotationMirror(TypeElement element, Class<?> clazz) {
        String clazzName = clazz.getName();
        for (AnnotationMirror m : element.getAnnotationMirrors()) {
            if (clazzName.equals(m.getAnnotationType().toString())) {
                return m;
            }
        }
        return null;
    }

    private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String key) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : annotationMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(key)) {
                return entry.getValue();
            }
        }
        return null;
    }

    @SuppressWarnings("SameParameterValue")
    @Nullable
    private static TypeMirror getAnnotationParam(TypeElement clazz, Class<?> annotationCls, String key) {
        AnnotationMirror anno = getAnnotationMirror(clazz, annotationCls);
        if (anno != null) {
            //log.info("anno = " + anno + " " + anno.getElementValues() + " " + anno.getAnnotationType());
            AnnotationValue param = getAnnotationValue(anno, key);
            if (param != null) {
                if (param instanceof UnresolvedClass) {
                    // log.info("param = " + param + " " + ((UnresolvedClass) param).type +
                    //         " " + ((UnresolvedClass) param).classType, true);
                    return ((UnresolvedClass) param).classType;
                } else if (param instanceof Attribute.Class) {
                    return ((Attribute.Class) param).getValue();
                }
            }
        }
        return null;
    }

    private static String dump(Map<TypeMirror, Set<TypeElement>> mapExpectToActual) {

        StringBuilder sb = new StringBuilder();
        for (Entry<TypeMirror, Set<TypeElement>> entry : mapExpectToActual.entrySet()) {
            TypeMirror expect = entry.getKey();
            Set<TypeElement> actuals = entry.getValue();

            sb.append("SpdtExpect [").append(expect).append("]:\n ");
            for (TypeElement actual : actuals) {
                SpdtActual anno = actual.getAnnotation(SpdtActual.class);
                sb.append("\tSpdtActual [").append(actual).append("] (").append(anno).append(")\n");
            }
        }
        return sb.toString();
    }

    private static final class TypeMirrorWrapper {

        private final TypeMirror actual;
        private final String key;

        private TypeMirrorWrapper(TypeMirror typeMirror) {
            if (typeMirror instanceof Type.ErrorType) {
                key = typeMirror.toString();
            } else {
                key = null;
            }
            actual = typeMirror;
        }

        static TypeMirrorWrapper of(TypeMirror typeMirror) {
            return new TypeMirrorWrapper(typeMirror);
        }

        @Override
        public int hashCode() {
            if (key == null) {
                return actual.hashCode();
            } else {
                return key.hashCode();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof TypeMirrorWrapper) {
                if (key == null) {
                    return actual.equals(((TypeMirrorWrapper) o).actual);
                } else {
                    return key.equals(((TypeMirrorWrapper) o).key);
                }
            }
            return false;
        }
    }

    @Override
    public Collection<String> getSupportAnnotations() {
        return Arrays.asList(
                "com.unionyy.mobile.spdt.annotation.SpdtActual",
                "com.unionyy.mobile.spdt.annotation.spdtExpect");
    }
}
