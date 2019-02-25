package com.unionyy.mobile.spdt.compiler.flavor;

import com.google.gson.Gson;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.unionyy.mobile.spdt.annotation.SpdtActual;
import com.unionyy.mobile.spdt.annotation.SpdtFlavor;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.IProcessor;
import com.unionyy.mobile.spdt.data.SpdtConfigData;
import com.unionyy.mobile.spdt.data.SpdtFlavorData;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

public class FlavorProcessor implements IProcessor {

    @Override
    public void process(final Env env, Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) throws Exception {
        String fileName = env.options.get("spdt_config_file");
        if (fileName == null) {
            return;
        }
        File file = new File(fileName);
        if (file.canRead()) {
            FileUtils.use(new BufferedReader(new FileReader(file)), new FileUtils.Action<BufferedReader>() {
                @Override
                public void run(BufferedReader reader) throws Exception {
                    Writer writer = new StringWriter();
                    FileUtils.copyTo(reader, writer);
                    processFlavorClass(env, writer.toString());
                }
            });
        }
    }

    private void processFlavorClass(Env env, String configContent) {
        env.messager.printMessage(Diagnostic.Kind.NOTE, "flavorConfig = " + configContent);

        SpdtConfigData config = new Gson().fromJson(configContent, SpdtConfigData.class);

        for (SpdtFlavorData flavor : config.getFlavors()) {

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PROTECTED)
                    .build();

            MethodSpec appid = MethodSpec.methodBuilder("getAppid")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(NotNull.class)
                    .addAnnotation(Override.class)
                    .returns(String.class)
                    .addCode("return $S;", flavor.getAppid())
                    .build();

            AnnotationSpec spdtActual = AnnotationSpec.builder(SpdtActual.class)
                    .addMember("value", "$T.class", ClassName.get(env.packageName, flavor.getFlavorName()))
                    .build();

            TypeSpec flavorCls = TypeSpec.classBuilder(flavor.getFlavorName())
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .addMethod(constructor)
                    .addMethod(appid)
                    .addSuperinterface(SpdtFlavor.class)
                    .addAnnotation(spdtActual)
                    .build();

            try {
                JavaFile.builder(env.packageName, flavorCls).build().writeTo(env.filer);
            } catch (IOException e) {
                env.messager.printMessage(Diagnostic.Kind.WARNING, e.getMessage());
            }
        }
    }

    @Override
    public List<String> getSupportedAnnotationTypes() {
        return Arrays.asList(
                "com.unionyy.mobile.spdt.annotation.SpdtActual",
                "com.unionyy.mobile.spdt.annotation.SpdtExpect",
                "com.unionyy.mobile.spdt.annotation.SpdtInject");
    }
}
