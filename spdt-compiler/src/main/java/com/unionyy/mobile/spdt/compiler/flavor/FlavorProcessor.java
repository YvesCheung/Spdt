package com.unionyy.mobile.spdt.compiler.flavor;

import com.google.auto.service.AutoService;
import com.google.gson.Gson;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.unionyy.mobile.spdt.annotation.SpdtFlavor;
import com.unionyy.mobile.spdt.compiler.Env;
import com.unionyy.mobile.spdt.compiler.SpdtProcessor;
import com.unionyy.mobile.spdt.data.SpdtConfigData;
import com.unionyy.mobile.spdt.data.SpdtFlavorData;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes(
        "com.unionyy.mobile.spdt.annotation.SpdtExpect"
)
public class FlavorProcessor extends SpdtProcessor {

    @Override
    protected void process(
            final Env env,
            Set<? extends TypeElement> set,
            RoundEnvironment roundEnvironment) throws Exception {

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
        env.logger.info("flavorConfig = " + configContent);

        SpdtConfigData config = new Gson().fromJson(configContent, SpdtConfigData.class);

        generateFlavor(env, config);
        generateFactory(env, config);
    }

    private void generateFlavor(Env env, SpdtConfigData config) {
        for (SpdtFlavorData flavor : config.getFlavors()) {

            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .build();

            MethodSpec appid = MethodSpec.methodBuilder("getAppid")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(NotNull.class)
                    .addAnnotation(Override.class)
                    .returns(String.class)
                    .addStatement("return $S", flavor.getAppid())
                    .build();

            TypeSpec flavorCls = TypeSpec.classBuilder(flavor.getFlavorName())
                    .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                    .addMethod(appid)
                    .addMethod(constructor)
                    .addSuperinterface(SpdtFlavor.class)
                    .build();

            try {
                JavaFile.builder(env.packageName + ".annotation", flavorCls)
                        .build()
                        .writeTo(env.filer);
            } catch (IOException e) {
                env.logger.warn(e.getMessage());
            }
        }
    }

    private void generateFactory(Env env, SpdtConfigData config) {
        ClassName spdtFlavor = ClassName.get(env.packageName + ".annotation",
                "SpdtFlavor");

        ClassName current = ClassName.get(env.packageName + ".annotation",
                config.getCurrentFlavor());
        ClassName factory = ClassName.get(env.packageName, "SpdtExpectToActualFactory");
        ParameterizedTypeName superFactory = ParameterizedTypeName.get(factory, spdtFlavor);

        MethodSpec createMethod = MethodSpec
                .methodBuilder("create")
                .returns(spdtFlavor)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addAnnotation(NotNull.class)
                .addStatement("return new $T()", current)
                .build();

        TypeSpec spdtFactory = TypeSpec
                .classBuilder("SpdtFlavor$$SpdtFactory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(createMethod)
                .addSuperinterface(superFactory)
                .build();

        try {
            JavaFile.builder(env.packageName + ".annotation", spdtFactory)
                    .build()
                    .writeTo(env.filer);
        } catch (IOException e) {
            env.logger.warn(e.getMessage());
        }
    }
}
