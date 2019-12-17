package com.unionyy.mobile.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.BaseVariant
import com.squareup.kotlinpoet.*
import com.unionyy.mobile.spdt.data.SpdtConfigData
import com.unionyy.mobile.spdt.data.SpdtFlavorData
import org.gradle.api.Project
import org.gradle.api.internal.DefaultDomainObjectSet
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask

class SpdtFlavorGenerator {

    static def apply(Project project, SpdtConfigContainer config) {
        project.pluginManager.withPlugin("com.android.library") {
            LibraryExtension ext = project.extensions.getByType(LibraryExtension)
            DefaultDomainObjectSet<? extends BaseVariant> variant = ext.libraryVariants
            configGenerateFlavorTask(project, variant, config)
        }
        project.pluginManager.withPlugin("com.android.application") {
            AppExtension ext = project.extensions.getByType(AppExtension)
            DefaultDomainObjectSet<? extends BaseVariant> variant = ext.applicationVariants
            configGenerateFlavorTask(project, variant, config)
        }
    }

    private static def configGenerateFlavorTask(
            Project project,
            DefaultDomainObjectSet<? extends BaseVariant> variants,
            SpdtConfigContainer config) {
        variants.all { variant ->
            def useAndroidX = project.findProperty("android.useAndroidX")
                    ?.toString()?.toBoolean() ?: false
            File outputDir =
                    new File(project.buildDir, "generated/source/spdt/${variant.dirName}")
            def task = project.tasks.create("generate${capitalize(variant.name)}SpdtFlavor")
            task.inputs.property("useAndroidX", useAndroidX)
            task.group = "spdt"

            variant.registerJavaGeneratingTask(task, outputDir)
            def generateBuildConfigTask = project.tasks.findByName(
                    "generate${capitalize(variant.name)}BuildConfig")
            if (generateBuildConfigTask != null) {
                generateBuildConfigTask.dependsOn task
            }
            println("SpdtPlugin: configGenerateFlavorTask, create ${capitalize(variant.name)}'s generate task")

            task.doLast {
                deleteFile(outputDir)
                writeJavaClass(outputDir, new SpdtConfigData(config.toList(), config.current.flavorName))
            }

            def kaptTask = project.tasks.findByName(
                    "kaptGenerateStubs${capitalize(variant.name)}Kotlin")
            if (kaptTask instanceof KaptGenerateStubsTask) {
                kaptTask.kotlinCompileTask.source(outputDir)
            } else {
                project.logger.warn("SpdtPlugin: Could not find the kaptGenerateStubs task!")
            }
        }
    }

    private static def writeJavaClass(File output, SpdtConfigData config) {
        logConfig(config)

        String spdtPackage = "com.unionyy.mobile.spdt"
        String annoPackage = spdtPackage + ".annotation"

        ClassName spdtFlavor = new ClassName(annoPackage, "SpdtFlavor")
        for (SpdtFlavorData flavor : config.getFlavors()) {

            FunSpec constructor = FunSpec.constructorBuilder()
                    .addModifiers(KModifier.INTERNAL)
                    .build()

            ClassName kotlinString = new ClassName("kotlin", "String")

            PropertySpec appid = PropertySpec.builder("appid", kotlinString)
                    .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                    .initializer("%S", flavor.getAppid())
                    .build()

            PropertySpec resourceSuffix = PropertySpec.builder("resourceSuffix", kotlinString)
                    .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                    .initializer("%S", flavor.getResourceSuffix())
                    .build()

            TypeSpec flavorCls = TypeSpec.classBuilder(flavor.getFlavorName())
                    .primaryConstructor(constructor)
                    .addModifiers(KModifier.FINAL, KModifier.PUBLIC)
                    .addProperty(appid)
                    .addProperty(resourceSuffix)
                    .addSuperinterface(spdtFlavor, CodeBlock.EMPTY)
                    .build()

            FileSpec.get(annoPackage, flavorCls)
                    .writeTo(output)
        }

        ClassName current = new ClassName(annoPackage, config.getCurrentFlavor())
        ClassName factory = new ClassName(spdtPackage, "SpdtExpectToActualFactory")
        ClassName spdtKeep = new ClassName(annoPackage, "SpdtKeep")

        ParameterizedTypeName superFactory = ParameterizedTypeName.get(factory, spdtFlavor)

        FunSpec createMethod = FunSpec
                .builder("create")
                .returns(spdtFlavor)
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .addStatement('return %T()', current)
                .build()

        TypeSpec spdtFactory = TypeSpec
                .classBuilder('SpdtFlavor-SpdtFactory')
                .addModifiers(KModifier.PUBLIC, KModifier.FINAL)
                .addAnnotation(spdtKeep)
                .addFunction(createMethod)
                .addSuperinterface(superFactory, CodeBlock.EMPTY)
                .build()

        FileSpec.get(annoPackage, spdtFactory)
                .writeTo(output)

    }

    private static def deleteFile(File file) {
        if (file.isDirectory()) {
            file.deleteDir()
        } else if (file.exists()) {
            file.delete()
        }
    }

    private static logConfig(SpdtConfigData config) {
        println "================================================================="
        println "Spdt plugin start to write java file!"
        println "config is " + config
        println "================================================================="
    }

    private static String capitalize(String self) {
        return self.length() > 0 && self.charAt(0).isLowerCase() ?
                self.substring(0, 1).toUpperCase() + self.substring(1) :
                self
    }
}
