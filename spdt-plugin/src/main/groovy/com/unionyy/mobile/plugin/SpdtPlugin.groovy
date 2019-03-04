package com.unionyy.mobile.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.google.gson.Gson
import com.unionyy.mobile.spdt.data.SpdtConfigData
import com.unionyy.mobile.spdt.data.SpdtFlavorData
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Namer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.internal.reflect.DirectInstantiator
import org.gradle.plugins.ide.eclipse.internal.AfterEvaluateHelper

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtPlugin implements Plugin<Project> {

    private def initializer = DirectInstantiator.INSTANCE

    private Namer<SpdtFlavorData> namer = new Namer<SpdtFlavorData>() {
        @Override
        String determineName(SpdtFlavorData spdtFlavor) {
            return spdtFlavor.flavorName
        }
    }

    @Override
    void apply(Project project) {
        println("Spdt apply ${project.name}")

        addDependency(project)

        def config = project.extensions.create(SpdtConfigContainer, "spdt", DefaultSpdtConfigContainer,
                initializer, namer)

        def file = project.file("spdt.tmp")
        processAptParam(project, file)

        project.afterEvaluate {
            if (!config.isEmpty()) {
                checkConfig(config)
                writeConfigToFile(config, file)
            }
        }
    }

    private static void addDependency(Project project) {
        def addSpdtDependency = new Action<AppliedPlugin>() {
            @Override
            void execute(AppliedPlugin appliedPlugin) {
                project.dependencies.add("implementation",
                        project.project("com.unionyy.mobile:spdt-api:1.0.0-SNAPSHOT"))
                project.dependencies.add("implementation",
                        project.project("com.unionyy.mobile:spdt-annotation:1.0.0-SNAPSHOT"))
            }
        }
        project.pluginManager.withPlugin("com.android.library", addSpdtDependency)
        project.pluginManager.withPlugin("com.android.application", addSpdtDependency)
        project.pluginManager.withPlugin("kotlin-android") {
            project.dependencies.add("kapt",
                    project.project("com.unionyy.mobile:spdt-compiler:1.0.0-SNAPSHOT"))
        }

        project.afterEvaluate {
            if ((project.plugins.hasPlugin('com.android.library')
                    || project.plugins.hasPlugin('com.android.application'))
                    && !project.plugins.hasPlugin('kotlin-android')) {
                project.dependencies.add("annotationProcessor",
                        project.project("com.unionyy.mobile:spdt-compiler:1.0.0-SNAPSHOT"))
            }
        }
    }

    private static writeConfigToFile(SpdtConfigContainer config, File file) {
        println "================================================================="
        println "Spdt plugin startup!"
        println "config is " + config
        println "================================================================="

        def serializeConfig = new SpdtConfigData(config.toList(), config.current.flavorName)
        file.write(new Gson().toJson(serializeConfig))
    }

    private static void checkConfig(SpdtConfigContainer configs) {

        if (configs.current == null) {
            throw new GradleException("Missing property of 'current' in 'spdt' config. " +
                    "Please specify the current flavor in [${configs.join(',')}]")
        }

        def reportJavaIdentifierError = {
            throw new GradleException("The flavor name '$it' in 'spdt' config is not a " +
                    "valid Java identifier.")
        }

        configs.each { SpdtFlavorData flavor ->
            def name = flavor.flavorName
            for (int i : 0..name.size() - 1) {
                char entry = name.charAt(i)
                if (i == 0) {
                    if (!Character.isJavaIdentifierStart(entry)) {
                        reportJavaIdentifierError(name)
                    }
                } else {
                    if (!Character.isJavaIdentifierPart(entry)) {
                        reportJavaIdentifierError(name)
                    }
                }
            }
        }
    }

    private void processAptParam(Project project, File configFile) {
        Action<? super AppliedPlugin> addAptParam = new Action<AppliedPlugin>() {
            @Override
            void execute(AppliedPlugin appliedPlugin) {
                if (project.android != null) {
                    project.android {
                        defaultConfig {
                            javaCompileOptions {
                                annotationProcessorOptions {
                                    it.argument('spdt_config_file', configFile.absolutePath)
                                }
                            }
                        }
                    }
                }
            }
        }
        project.pluginManager.withPlugin("com.android.library", addAptParam)
        project.pluginManager.withPlugin("com.android.application", addAptParam)
    }
}
