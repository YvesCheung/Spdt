package com.unionyy.mobile.spdt.compiler.expect

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.sun.tools.javac.code.Type
import com.sun.tools.javac.code.Type.ClassType
import com.unionyy.mobile.spdt.annotation.SpdtActual
import com.unionyy.mobile.spdt.annotation.SpdtApp
import com.unionyy.mobile.spdt.annotation.SpdtExpect
import com.unionyy.mobile.spdt.annotation.SpdtIndex
import com.unionyy.mobile.spdt.annotation.SpdtKeep
import com.unionyy.mobile.spdt.api.DefaultFlavor
import com.unionyy.mobile.spdt.compiler.Env
import com.unionyy.mobile.spdt.compiler.IProcessor
import com.unionyy.mobile.spdt.compiler.Logger
import java.io.IOException
import java.util.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import kotlin.math.abs

class ExpectProcessor : IProcessor {

    companion object {

        const val PKG_NAME = "com.unionyy.mobile.spdt.factory"
    }

    private lateinit var log: Logger

    @Throws(Exception::class)
    override fun process(
        env: Env,
        set: Set<TypeElement>,
        roundEnvironment: RoundEnvironment
    ) {
        log = env.logger

        val actualClasses = ElementFilter.typesIn(
            roundEnvironment.getElementsAnnotatedWith(SpdtActual::class.java))

        val mapExpectToActual = classifyActualClass(actualClasses)
        log.info(dump(mapExpectToActual), true)

        generateFactoryClass(env, roundEnvironment, mapExpectToActual)
    }

    private fun classifyActualClass(actualClasses: Set<TypeElement>): Map<TypeMirror, Set<TypeElement>> {
        val result = LinkedHashMap<TypeMirror, MutableSet<TypeElement>>()
        for (actualCls in actualClasses) {
            val interfaces = actualCls.interfaces
            val superCls = actualCls.superclass

            if (actualCls is Type && (actualCls as Type).isInterface) {
                log.error("The type [" + actualCls + "] is annotated with @SpdtActual,\n" +
                    "so it can't be an interface.\n" +
                    "Use @SpdtActual class " + actualCls + " instead.")
            }
            if (actualCls.modifiers.contains(Modifier.ABSTRACT)) {
                log.error("The type [" + actualCls + "] is annotated with @SpdtActual,\n" +
                    "so it can't be abstract.\n" +
                    "Use @SpdtActual class " + actualCls + " instead.")
            }
            if (interfaces.isEmpty() && (superCls == null || isObject(superCls))) {
                log.error("The class [" + actualCls + "] is annotated with @SpdtActual,\n" +
                    "but it has no super class or interfaces.\n" +
                    "Which class or interfaces do you want 'Spdt' to bind?")
            }

            val expectClasses = findExpectTarget(superCls, interfaces)
            if (expectClasses.isEmpty()) {

                val superCandidate: Collection<TypeMirror>
                if (isObject(superCls)) {
                    superCandidate = interfaces
                } else {
                    val tmp = LinkedHashSet(interfaces)
                    tmp.add(superCls)
                    superCandidate = tmp
                }

                if (superCandidate.isEmpty()) {
                    log.error("The class [" + actualCls + "] is annotated with @SpdtActual,\n" +
                        "but no @SpdtExpect target found.\n" +
                        "You should let it implement an interface.")
                } else {
                    log.error("The class [" + actualCls + "] is annotated with @SpdtActual,\n" +
                        "but no @SpdtExpect target found.\n" +
                        "You should add @SpdtExpect annotation to one of the following target:\n" +
                        "<" + superCandidate + ">.\n" +
                        "Or allow to implement only one interface without annotations.")
                }
            }

            for (expectCls in expectClasses) {
                var actualClsForThisExpect: MutableSet<TypeElement>? = result[expectCls]
                if (actualClsForThisExpect == null) {
                    actualClsForThisExpect = LinkedHashSet()
                    result[expectCls] = actualClsForThisExpect
                }
                actualClsForThisExpect.add(actualCls)
            }
        }

        for ((expect, actuals) in result) {

            val checkDuplicate = LinkedHashMap<TypeMirrorWrapper, TypeElement>()
            for (actualCls in actuals) {
                val allFlavor = getAnnotationParam(actualCls)

                for (f in allFlavor) {
                    val wrapper = TypeMirrorWrapper.of(f)
                    val otherCls = checkDuplicate[wrapper]
                    if (otherCls != null) {
                        log.error("The class [" + otherCls + "] and [" + actualCls + "] are \n" +
                            "annotated with the same annotation [@SpdtActual(" + f + ")],\n" +
                            "and they implement the same interface or inherit the same base class " +
                            "@SpdtExpect [" + expect + "].\n" +
                            "Each flavor can have only one implementation class.")
                    } else {
                        checkDuplicate[wrapper] = actualCls
                    }
                }
            }
        }
        return result
    }

    private fun findExpectTarget(
        superClass: TypeMirror?,
        interfaces: Collection<TypeMirror>
    ): Set<TypeMirror> {
        val result = LinkedHashSet<TypeMirror>()
        val clsOrInterfaces = LinkedHashSet<TypeMirror?>()
        clsOrInterfaces.add(superClass)
        clsOrInterfaces.addAll(interfaces)

        for (cls in clsOrInterfaces) {
            if (cls is ClassType) {
                val maybeNull = cls.asElement()
                    .getAnnotation(SpdtExpect::class.java)
                if (maybeNull != null) {
                    result.add(cls)
                }
            }
        }

        //Trick: if no @SpdtExpect element，use the single interface.
        if (result.isEmpty()
            && isObject(superClass)
            && interfaces.size == 1) {
            val singleInterface = interfaces.iterator().next()
            result.add(singleInterface)
        }
        return result
    }


    private fun generateFactoryClass(
        env: Env,
        roundEnvironment: RoundEnvironment,
        mapExpectToActual: Map<TypeMirror, Set<TypeElement>>
    ) {
        val appElement =
            roundEnvironment.getElementsAnnotatedWith(SpdtApp::class.java)
        val isApp = appElement != null && appElement.isNotEmpty()
        val generator = if (isApp) AppFactoryGenerator() else LibraryFactoryGenerator()
        generator.generateFactoryClass(env, mapExpectToActual)
    }

    private fun isObject(obj: TypeMirror?): Boolean {
        return obj is ClassType && "java.lang.Object" == obj.toString()
    }

    private fun hasDefaultFlavor(flavors: Collection<TypeMirror>): Boolean {
        for (type in flavors) {
            if (type.toString() == DefaultFlavor::class.java.canonicalName) {
                return true
            }
        }
        return false
    }

    private fun getAnnotationParam(actualCls: TypeElement): Set<TypeMirror> {
        val allFlavor = LinkedHashSet<TypeMirror>()
        val anno = actualCls.getAnnotation(SpdtActual::class.java)
        try {
            anno.values
        } catch (e: MirroredTypesException) {
            allFlavor.addAll(e.typeMirrors)
        }

        try {
            anno.value
        } catch (e: MirroredTypeException) {
            val type = e.typeMirror
            if (type != null && type.toString() != DefaultFlavor::class.java.canonicalName) {
                allFlavor.add(type)
            }
        }

        return allFlavor
    }

    private fun dump(mapExpectToActual: Map<TypeMirror, Set<TypeElement>>): String {

        val sb = StringBuilder()
        for ((expect, actuals) in mapExpectToActual) {

            sb.append("SpdtExpect [").append(expect).append("]:\n ")
            for (actual in actuals) {
                val anno = actual.getAnnotation(SpdtActual::class.java)
                sb.append("\tSpdtActual [").append(actual).append("] (").append(anno).append(")\n")
            }
        }
        return sb.toString()
    }

    private class TypeMirrorWrapper {

        private val actual: TypeMirror?
        private val key: String?

        private constructor(typeMirror: TypeMirror) {
            if (typeMirror is Type.ErrorType) {
                key = typeMirror.toString()
            } else {
                key = null
            }
            actual = typeMirror
        }

        private constructor(key: String) {
            this.key = key
            actual = null
        }

        override fun hashCode(): Int {
            return key?.hashCode() ?: actual!!.hashCode()
        }

        override fun equals(other: Any?): Boolean {
            return if (other is TypeMirrorWrapper) {
                if (key == null) {
                    actual == other.actual
                } else {
                    key == other.key
                }
            } else false
        }

        companion object {

            internal fun of(typeMirror: TypeMirror): TypeMirrorWrapper {
                return TypeMirrorWrapper(typeMirror)
            }

            internal fun of(type: Class<*>): TypeMirrorWrapper {
                return TypeMirrorWrapper(type.canonicalName)
            }
        }
    }

    override fun getSupportAnnotations(): Collection<String> {
        return listOf(
            SpdtActual::class.java.canonicalName,
            SpdtExpect::class.java.canonicalName)
    }

    interface ExpectToActualFactoryGenerator {

        fun generateFactoryClass(env: Env, mapExpectToActual: Map<TypeMirror, Set<TypeElement>>)
    }

    private inner class LibraryFactoryGenerator : ExpectToActualFactoryGenerator {

        override fun generateFactoryClass(env: Env, mapExpectToActual: Map<TypeMirror, Set<TypeElement>>) {
            val moduleName = (env.options["spdt_module_name"]?.capitalize()
                ?: "Spdt" + UUID.randomUUID().toString()).replace("-", "_")
            for ((expectCls, value) in mapExpectToActual) {
                val factoryName = ClassName(env.packageName, "SpdtExpectToActualFactory")
                val expectClsName = ClassName.bestGuess(expectCls.toString())

                val createMethod = generateCreateFunction(env, expectClsName, value)

                val indexAnnotation = AnnotationSpec.builder(SpdtIndex::class)
                    .addMember("clsName = %S", expectClsName)
                    .build()

                val factoryCls = TypeSpec
                    .classBuilder(moduleName +
                        "${abs(expectClsName.reflectionName().hashCode())}" +
                        "SpdtFactory")
                    .addSuperinterface(factoryName.parameterizedBy(expectClsName))
                    .addModifiers(KModifier.FINAL, KModifier.PUBLIC)
                    .addFunction(createMethod)
                    .addAnnotation(indexAnnotation)
                    .build()

                try {
                    FileSpec.get(PKG_NAME, factoryCls).writeTo(env.filer)
                } catch (e: IOException) {
                    log.warn(e.message)
                }
            }
        }
    }

    private inner class AppFactoryGenerator : ExpectToActualFactoryGenerator {

        override fun generateFactoryClass(env: Env, mapExpectToActual: Map<TypeMirror, Set<TypeElement>>) {
            for ((expectCls, value) in mapExpectToActual) {
                val expectClsName = ClassName.bestGuess(expectCls.toString())
                val factoryName = ClassName(env.packageName, "SpdtExpectToActualFactory")

                val createMethod = generateCreateFunction(env, expectClsName, value)

                val className = expectClsName.reflectionName()
                    .replaceFirst((expectClsName.packageName + "."), "")

                val factoryCls = TypeSpec
                    .classBuilder("$className-SpdtFactory")
                    .addSuperinterface(factoryName.parameterizedBy(expectClsName))
                    .addModifiers(KModifier.FINAL, KModifier.PUBLIC)
                    .addFunction(createMethod)
                    .addAnnotation(SpdtKeep::class)
                    .build()

                try {
                    FileSpec.get(expectClsName.packageName, factoryCls).writeTo(env.filer)
                } catch (e: IOException) {
                    log.warn(e.message)
                }
            }
        }
    }

    private fun generateCreateFunction(
        env: Env,
        expectCls: ClassName,
        value: Set<TypeElement>
    ): FunSpec {

        val spdtCls = ClassName(env.packageName, "Spdt")

        val createMethodBuilder = FunSpec.builder("create")
            .addModifiers(KModifier.OVERRIDE)
            .returns(expectCls.copy(nullable = true))
            .addStatement("val flavorCls = %T.currentFlavor()::class", spdtCls)

        var defaultFlavor: TypeElement? = null
        for (actualCls in value) {
            val flavors = getAnnotationParam(actualCls)
            if (flavors.isEmpty()) {
                defaultFlavor = actualCls
                continue
            } else if (hasDefaultFlavor(flavors)) {
                defaultFlavor = actualCls
            }
            for (flavor in flavors) {
                val flavorName =
                    if (flavor is Type.ErrorType) {
                        val origin = flavor.toString()
                        val guessCls = ClassName.bestGuess(origin.substring(origin.indexOf(".") + 1))
                        if (guessCls.packageName.isEmpty()) {
                            ClassName(env.packageName + ".annotation", guessCls.toString())
                        } else {
                            guessCls
                        }
                    } else {
                        flavor.asTypeName()
                    }
                createMethodBuilder.addCode(
                    CodeBlock
                        .builder()
                        .beginControlFlow("if (flavorCls == %T::class)", flavorName)
                        .addStatement("return %T()", actualCls)
                        .endControlFlow()
                        .build()
                )
            }
        }

        return if (defaultFlavor != null) {
            createMethodBuilder
                .addStatement("return %T()", defaultFlavor)
                .build()
        } else {
            createMethodBuilder
                .addStatement("return null")
                .build()
        }
    }
}
