package com.unionyy.mobile.plugin

import com.google.gson.Gson
import com.unionyy.mobile.spdt.data.SpdtConfigData
import com.unionyy.mobile.spdt.data.SpdtFlavorData
import org.gradle.api.*
import org.gradle.api.plugins.AppliedPlugin
import org.gradle.internal.reflect.DirectInstantiator

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtPlugin implements Plugin<Project> {

    private def initializer = DirectInstantiator.INSTANCE

    private def namer = new Namer<SpdtFlavorData>() {
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

//        def file = project.file("spdt.tmp")
//        processAptParam(project, file)

        project.afterEvaluate {
            if (!config.isEmpty()) {
                checkConfig(config)
                SpdtFlavorGenerator.apply(project, config)
                //writeConfigToFile(config, file)
            }
        }

    }

    private static void addDependency(Project project) {

        def version = "1.0.7-SNAPSHOT"

        def getDepend = { String module ->
            def moduleProject = project.findProject(":$module")
            if (moduleProject != null) {
                return moduleProject
            } else {
                return "com.unionyy.mobile:$module:$version"
            }
        }

        def addSpdtDependency = new Action<AppliedPlugin>() {
            @Override
            void execute(AppliedPlugin appliedPlugin) {
                project.dependencies.add("implementation", getDepend("spdt-api"))
                project.dependencies.add("implementation", getDepend("spdt-annotation"))
            }
        }
        project.pluginManager.withPlugin("com.android.library", addSpdtDependency)
        project.pluginManager.withPlugin("com.android.application", addSpdtDependency)
        project.pluginManager.withPlugin("kotlin-android") {
            project.dependencies.add("kapt", getDepend("spdt-compiler"))
        }

        project.afterEvaluate {
            if ((project.plugins.hasPlugin('com.android.library')
                    || project.plugins.hasPlugin('com.android.application'))
                    && !project.plugins.hasPlugin('kotlin-android')) {
                project.dependencies.add("annotationProcessor", getDepend("spdt-compiler"))
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
            throw new GradleException("The $it in 'spdt' config is not a " +
                    "valid Java identifier.")
        }

        configs.each { SpdtFlavorData flavor ->
            def name = flavor.flavorName
            if (name.isEmpty()) {
                reportJavaIdentifierError("empty flavor name ''")
            }
            for (int i : 0..name.size() - 1) {
                char entry = name.charAt(i)
                if (i == 0) {
                    if (!Character.isJavaIdentifierStart(entry)) {
                        reportJavaIdentifierError("flavor name '$name'")
                    }
                } else {
                    if (!Character.isJavaIdentifierPart(entry)) {
                        reportJavaIdentifierError("flavor name '$name'")
                    }
                }
            }

            def suffix = flavor.resourceSuffix
            if (suffix.size() > 0) {
                for (int i : 0..suffix.size() - 1) {
                    if (!Character.isJavaIdentifierPart(suffix.charAt(i))) {
                        reportJavaIdentifierError("resourceSuffix '$suffix'")
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
