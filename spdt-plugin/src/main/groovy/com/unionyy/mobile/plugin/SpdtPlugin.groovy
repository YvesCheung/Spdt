package com.unionyy.mobile.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import com.google.gson.Gson
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by 张宇 on 2019/2/21.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SpdtPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("Spdt apply ${project.name}")

        def config = project.extensions.create("spdt", SpdtConfig)

        def file = project.file("spdt.tmp")
        processAptParam(project, file)

        project.afterEvaluate {
            if (!config.flavors.isEmpty()) {
                checkConfig(config)
                writeConfigToFile(config, file)
            }
        }

    }

    private static writeConfigToFile(SpdtConfig config, File file) {
        println "================================================================="
        println "Spdt plugin startup!"
        println "config is " + config.flavors
        println "================================================================="

        file.write(new Gson().toJson(config.flavors))
    }

    private static void checkConfig(SpdtConfig config) {

        def reportError = {
            throw new GradleException("The flavor name '$it' in 'spdt' config is not a valid Java identifier.")
        }

        config.flavors.each { flavor ->
            def name = flavor.flavorName
            for (int i : 0..name.size() - 1) {
                def entry = name.charAt(i)
                if (i == 0) {
                    if (!Character.isJavaIdentifierStart(entry)) {
                        reportError(name)
                    }
                } else {
                    if (!Character.isJavaIdentifierPart(entry)) {
                        reportError(name)
                    }
                }
            }
        }
    }

    private void processAptParam(Project project, File configFile) {
        if ((project.plugins.hasPlugin(AppPlugin)
                || project.plugins.hasPlugin(LibraryPlugin))
                && project.android != null) {

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
