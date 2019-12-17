package com.unionyy.mobile.spdt.compiler.expect

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.unionyy.mobile.spdt.annotation.SpdtApp
import com.unionyy.mobile.spdt.annotation.SpdtIndex
import com.unionyy.mobile.spdt.annotation.SpdtKeep
import com.unionyy.mobile.spdt.compiler.Env
import com.unionyy.mobile.spdt.compiler.IProcessor
import com.unionyy.mobile.spdt.compiler.Logger
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter

/**
 * @author YvesCheung
 * 2019-12-16
 */
class ActualFactoryProcessor : IProcessor {

    private lateinit var log: Logger

    override fun process(env: Env, set: MutableSet<out TypeElement>, roundEnvironment: RoundEnvironment) {
        log = env.logger

        val app = roundEnvironment.getElementsAnnotatedWith(SpdtApp::class.java)
        if (app.isNotEmpty()) {
            if (app.size > 1) {
                log.error("More than one @SpdtApp class found!")
            } else {
                //map expect className -> factory class list
                val map = mutableMapOf<String, MutableList<TypeElement>>()
                val genPackage = env.elements.getPackageElement(ExpectProcessor.PKG_NAME)
                    ?: return
                for (element in ElementFilter.typesIn(genPackage.enclosedElements)) {
                    log.info("middle factory = $element")
                    val index = element.getAnnotation(SpdtIndex::class.java)
                    if (index != null) {
                        val list = map[index.clsName]
                        if (list != null) {
                            list.add(element)
                        } else {
                            map[index.clsName] = mutableListOf(element)
                        }
                    }
                }

                generateFactory(env, map)
            }
        }
    }

    private fun generateFactory(env: Env, map: Map<String, List<TypeElement>>) {
        val factoryName = ClassName(env.packageName, "SpdtExpectToActualFactory")

        for ((clsName, classes) in map.entries) {
            log.info("expect = $clsName\n factoryCls = $classes", true)
            val expectClsName = ClassName.bestGuess(clsName)

            val code = CodeBlock.builder().add("listOf(")
            for ((idx, cls) in classes.withIndex()) {
                if (idx == 0) code.add("%T()", cls)
                else code.add(", %T()", cls)
            }
            code.add(")")

            val createMethod = FunSpec.builder("create")
                .addModifiers(KModifier.OVERRIDE)
                .returns(expectClsName.copy(nullable = true))
                .addCode(CodeBlock.builder()
                    .beginControlFlow("for(factory in %L)", code.build())
                    .addStatement("val result = factory.create()")
                    .beginControlFlow("if (result != null)")
                    .addStatement("return result")
                    .endControlFlow()
                    .endControlFlow()
                    .addStatement("return null")
                    .build())
                .build()

            val expectClsSimpleName = expectClsName.simpleNames.joinToString(separator = "$")
            val typeName = "$expectClsSimpleName-SpdtFactory"
            val factoryCls = TypeSpec
                .classBuilder(typeName)
                .addSuperinterface(factoryName.parameterizedBy(expectClsName))
                .addFunction(createMethod)
                .addAnnotation(SpdtKeep::class)
                .build()

            FileSpec.builder(expectClsName.packageName, typeName)
                .addType(factoryCls)
                .build()
                .writeTo(env.filer)
        }
    }

    override fun getSupportAnnotations(): Collection<String> =
        listOf(SpdtApp::class.java.canonicalName,
            SpdtIndex::class.java.canonicalName)

}